import React, {useCallback, useContext, useEffect, useState, useMemo} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import "./ResultComponent.css";
import Box from "@mui/material/Box";
import {Button, CircularProgress, Dialog, DialogContent, Paper} from "@mui/material";
import {message, DatePicker, Tabs, Rate, Typography, Tag, List, Avatar, Card, Image} from "antd";
import InstructionComponent from "./InstructionComponent";
import TabPane from "antd/es/tabs/TabPane";
import IngredientComponent from "./IngredientComponent";
import NutritionComponent from "./NutritionComponent";
import {AuthContext} from "../AuthContext";
import axios from "axios";
import LoginWindowComponent from "../LoginWindowComponent/LoginWindowComponent";
import dayjs from "dayjs";
import {FieldTimeOutlined, UploadOutlined} from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";
import Dragger from "antd/es/upload/Dragger";

const ResultComponent = () => {
    const location = useLocation();
    const {recipeId} = location.state;
    const {user} = useContext(AuthContext);
    const navigate = useNavigate();
    const [loginModalVisible, setLoginModalVisible] = useState(false);
    const [addToMealPlanVisible, setAddToMealPlanVisible] = useState(false);
    const [selectedDate, setSelectedDate] = useState(dayjs());
    const [recipe, setRecipe] = useState(null);
    const [loading, setLoading] = useState(true);
    const [fileList, setFileList] = useState([]);
    const [userRate, setUserRate] = useState(0.0);

    const defaultRecipe = useMemo(() => ({
        name: "No Name available",
        description: "No description available",
        rating: 0,
        recipeApiId: null,
        image: "",
        instructions: [],
        ingredients: [],
        nutrition: []
    }), []);

    const [comments, setComments] = useState([]);

    const [newComment, setNewComment] = useState("");

    // Handle image upload
    const handleUpload = (file) => {
        if (fileList.length >= 3) {
            message.error("You can only upload up to 3 images.");
            return false; // Prevent upload
        }

        const reader = new FileReader();
        reader.onload = (e) => handleFileRead(e, file);
        reader.readAsDataURL(file);

        return true; // Prevent auto-upload to server
    };

    const handleFileRead = (e, file) => {
        const newFile = {
            uid: file.uid,
            name: file.name,
            url: e.target.result, // Store as base64
            file,
        };

        setFileList((prev) => [...prev.filter((f) => f.uid !== file.uid), newFile]);
    };


    // Handle submit comment
    const handleSubmit = async () => {
        if (!newComment.trim()) {
            message.error("Comment cannot be empty!");
            return;
        }
        if (user == null) {
            message.info("Login Required");
            setLoginModalVisible(true);
            return;
        }

        const commentDTO = {
            userId: user.id,
            recipeApiId: recipeId,
            comment: newComment,
            images: fileList.map(file => file.url.split(',')[1]), // Extract base64 part
            rate: userRate
        };

        try {
            const response = await axios.post("http://localhost:8080/api/comment/save", commentDTO, {
                withCredentials: true
            });

            if (response.status === 200) {
                message.success("Comment posted successfully");
                setNewComment("");
                setFileList([]);
                setUserRate(0);
                fetchRecipeComments();
            } else {
                message.error("Failed to post comment");
            }
        } catch (error) {
            console.error("Error posting comment: ", error);
            message.error("Failed to post comment");
        }
    };

    const handleChange = ({ fileList: updatedList }) => {

        if (updatedList.length > 3) {
            message.error("You can only upload up to 3 images.");
            updatedList = updatedList.slice(0, 3);
        }

        setFileList(updatedList.map((file) => ({
            uid: file.uid,
            name: file.name,
            url: file.url || file.thumbUrl || file.preview || "", // Ensure a valid preview URL
            file,
        })));
    };

    const handleRateChange = (value) => {
        setUserRate(value);
        console.log(value);
    }

    const fetchRecipeComments = useCallback(async () => {
        if (!recipeId) {
            message.error("Failed to get recipe id");
            navigate("/");
            return;
        }
        try {
            const response = await axios.get(`http://localhost:8080/api/comment?recipeId=${recipeId}`);
            if (response.status === 200) {
                setComments(response.data);
                console.log(response.data);
            } else {
                message.error("Fail to get comments");
            }
        } catch (error) {
            console.log(error);
            message.error("Failed to get comments");
        }
    }, [recipeId, navigate]);


    useEffect(() => {
        console.log("Location state:", location.state);
        const fetchRecipeById = async () => {
            if (!recipeId) {
                message.error("Failed to get recipe id");
                navigate("/");
                return;
            }
            try {
                const response = await axios.get(`http://localhost:8080/api/recipe/id?queryId=${recipeId}`);
                if(response.status === 200) {
                    const fetchedRecipe = response.data;
                    setRecipe({
                        name: fetchedRecipe.name || "No Name available",
                        description: fetchedRecipe.description || "No description available",
                        rate: fetchedRecipe.rate || 0,
                        recipeApiId: fetchedRecipe.recipeApiId,
                        image: fetchedRecipe.image,
                        instructions: fetchedRecipe.instructions || [],
                        ingredients: fetchedRecipe.ingredients || [],
                        nutrition: fetchedRecipe.nutrition || [],
                        cookTime: fetchedRecipe.cookTime || "N/A",
                        glutenFree: fetchedRecipe.glutenFree || false,
                        dairyFree: fetchedRecipe.dairyFree || false,
                        vegetarian: fetchedRecipe.vegetarian || false
                    });
                    console.log(response.data);
                } else {
                    message.error("Failed to get recipe information");
                    console.log("error");
                    setRecipe(defaultRecipe);
                    navigate("/");
                }
            } catch (error) {
                message.error("Failed to get recipe information due to unexpected error");
                console.log(error);
                setRecipe(defaultRecipe);
                navigate("/");
            } finally {
                setLoading(false);
            }
        };

        fetchRecipeById();
        fetchRecipeComments();

    }, [recipeId, location.state, defaultRecipe, fetchRecipeComments, navigate]);

    const handleLoginModalCancel = () => {
        setLoginModalVisible(false);
    };

    const handleMealPlanModalCancel = () => {
        setAddToMealPlanVisible(false);
    };

    const handleDateChange = (date) => {
        setSelectedDate(date);
    };

    const handleMealPlanFormSubmit = async () => {
        if (user != null) {
            if (selectedDate == null) {
                message.error("Please select a valid date!");
            }
            else {
                const formattedDate = selectedDate.format("YYYY-MM-DD");
                try {
                    const response = await axios.put(
                        "http://localhost:8080/api/mealplan/update",
                        recipe,
                        {
                            params: {
                                userId: user.id,
                                date: formattedDate
                            },
                            withCredentials: true
                        }
                    )
                    if (response.status === 200) {
                        console.log("Recipe successfully added into meal plan");
                        message.success("Added recipe into meal plan successfully.");
                    } else {
                        console.log("Failed to add recipe into meal plan");
                        message.error("Failed to add recipe into meal plan, please try again.");
                    }
                } catch (error) {
                    if (error && error.response && error.response.status === 409) {
                        message.error("Recipe already in meal plan");
                    } else {
                        console.error("error adding recipe into meal plan: ", error);
                        message.error("An unexpected error happened, please try again");
                    }
                }
            }
            handleMealPlanModalCancel(); // Close the modal after submission
        } else {
            message.info("Login Required");
            handleMealPlanModalCancel();
            setLoginModalVisible(true);
        }
    };

    const handleAddToMealPlan = () => {
        if (user != null) {
            setAddToMealPlanVisible(true);
        } else {
            message.info("Login Required");
            setLoginModalVisible(true);
        }
    }

    const handleAddToFavorites = async () => {
        if (user != null) {
            try {
                const response = await axios.post(
                    "http://localhost:8080/api/recipe/save",
                    recipe,
                    {
                        params: { userId: user.id },
                        withCredentials: true,
                    }
                );
                if (response.status === 200) {
                    console.log("Recipe added to favorites successfully");
                    message.success("Save recipe to favourite successfully");
                } else {
                    message.error("Failed to save recipe into favourite");
                    console.error("Failed to add recipe to favorites");
                }
            } catch (error) {
                if (error && error.response && error.response.status === 409) {
                    message.error("Recipe already in favourites");
                }
                else {
                    message.error("Failed to save recipe into favourite");
                    console.log("error occurred when adding to favourite");
                }
            }
        } else {
            message.info("Login Required")
            setLoginModalVisible(true);
        }
    }
    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box className={"resultContainer"}>
            <Box className={"resultTopContainer"}>
                <Paper elevation={2} className={"topInfoPaper"}>
                    <Box className={"topLeftInfoContainer"}>
                        <Box className={"recipeBasicInfo"}>
                            <h1>{recipe.name}</h1>
                            <Rate disabled={true} allowHalf defaultValue={recipe.rate}/>
                                <Typography className={"detailedDescription"}>
                                    {recipe.description}
                            </Typography>
                            <div className={"resultTagContainer"}>
                                <Tag icon={<FieldTimeOutlined />} className="resultTags">
                                    Cook Time : {recipe.cookTime} min
                                </Tag>
                                <div className={"resultTagContainer"}>
                                    {recipe.glutenFree && (
                                        <Tag color="green" className="resultTags">
                                            Gluten Free
                                        </Tag>
                                    )}
                                    {recipe.dairyFree && (
                                        <Tag color="blue" className="resultTags">
                                            Dairy Free
                                        </Tag>
                                    )}
                                    {recipe.vegetarian && (
                                        <Tag color="orange" className="resultTags">
                                            Vegetarian
                                        </Tag>
                                    )}
                                </div>
                            </div>
                        </Box>
                        <Box className={"resultButtons"}>
                            <Button variant="contained" className={"resultButton"}
                                    onClick={handleAddToFavorites}>
                                Add Recipe to Favorites
                            </Button>
                            <Button variant="contained" className={"resultButton"}
                                    onClick={handleAddToMealPlan}>
                                Add Recipe to Meal Plan
                            </Button>
                        </Box>
                    </Box>
                    <Box className={"resultImageContainer"}>
                        <Paper className={"resultImagePaper"}>
                            <img src={recipe.image} alt={"recognize"} className={"resultImage"}/>
                        </Paper>
                    </Box>
                </Paper>
            </Box>

            <Box className={"resultBottomContainer"}>
                <Paper className={"resultBottomPaper"}>
                    <Tabs defaultActiveKey="1" tabPosition="bottom" centered className="customTabs">
                        <TabPane tab="Instructions" key="1">
                            <InstructionComponent instructions={recipe.instructions || []}/>
                        </TabPane>
                        <TabPane tab="Ingredients" key="2">
                            <IngredientComponent ingredients={recipe.ingredients}/>
                        </TabPane>
                        <TabPane tab="Nutrition" key="3">
                            <NutritionComponent nutrition={recipe.nutrition}/>
                        </TabPane>
                    </Tabs>
                </Paper>
            </Box>

            <Box className={"commentContainer"}>
                <Paper className={"commentPaper"}>
                    {!comments || comments.length === 0 ? (
                        <div style={{ textAlign: "center", padding: "20px", fontSize: "16px", color: "#888" }}>
                            No comments available
                        </div>
                    ) : (
                        <List
                            itemLayout="vertical"
                            size="large"
                            pagination={{ pageSize: 3 }}
                            dataSource={comments}
                            locale={{ emptyText: null }} // Hide default "No Data" message
                            renderItem={(item) => (
                                <List.Item key={item.userName}>
                                    <List.Item.Meta
                                        avatar={
                                            <div className="commentImageContainer">
                                                <Avatar
                                                    className="commentUserImage"
                                                    src={item.userPhoto? item.userPhoto : "https://api.dicebear.com/7.x/miniavs/svg?seed=1"}
                                                    alt="User Avatar"
                                                />
                                            </div>
                                        }
                                        title={<p className="commentUsername">{item.userName}</p>}
                                        description={<Rate disabled value={item.rate} allowHalf />}
                                    />
                                    <p>{item.comment}</p>
                                    {item.images.map((image, index) => (
                                        <Image
                                            key={`${image}-${index}`}
                                            src={`data:image/jpeg;base64,${image}`}
                                            alt={`comment-image-${index}`}
                                            style={{ width: 100, height: 100, objectFit: "cover", margin: "5px" }}
                                        />
                                    ))}
                                </List.Item>
                            )}
                        />
                    )}
                    <div>
                        {/* Comment Input Section */}
                        <Card style={{ marginBottom: 20 }}>
                            <Typography.Title level={3}>Write a Comment</Typography.Title>
                            <Rate onChange={handleRateChange}/>
                            <TextArea
                                rows={3}
                                placeholder="Write a comment..."
                                value={newComment}
                                onChange={(e) => setNewComment(e.target.value)}
                            />
                            <Dragger
                                beforeUpload={handleUpload}
                                fileList={fileList}
                                onChange={handleChange}
                                showUploadList={{ showPreviewIcon: true }}
                                accept="image/*"
                                multiple
                                maxCount={3}
                                style={{ marginTop: 10 }}
                            >
                                <p className="ant-upload-drag-icon">
                                    <UploadOutlined />
                                </p>
                                <p className="ant-upload-text">Click or drag image to upload</p>
                                <p className="ant-upload-hint">Supports multiple images (Max: 3)</p>
                            </Dragger>

                            <div style={{ display: "flex", gap: "10px", margin: "10px 0", flexWrap: "wrap" }}>
                                {fileList.map((img, index) => (
                                    <div key={`${img}-${index}`} style={{ textAlign: "center" }}>
                                        <img
                                            src={img.url}
                                            alt={`upload-preview-${index}`}
                                            style={{ width: 100, height: 100, objectFit: "cover", borderRadius: 5 }}
                                        />
                                    </div>
                                ))}
                            </div>

                            <Button type="primary" onClick={handleSubmit}>Post Comment</Button>
                        </Card>
                    </div>
                </Paper>
            </Box>
            <Dialog
                open={loginModalVisible}
                onClose={handleLoginModalCancel}
                centered
            >
                <DialogContent sx={{ width: '500px', padding:0 }}>
                    <LoginWindowComponent redirectOnLogin={false} onLoginSuccess={handleLoginModalCancel}/>
                </DialogContent>
            </Dialog>

            <Dialog
                open={addToMealPlanVisible}
                onClose={handleMealPlanModalCancel}
                centered
            >
                <DialogContent>
                    <div>
                        <h3>Select Meal Plan Date</h3>
                        <DatePicker
                            value={selectedDate}
                            onChange={handleDateChange}
                            defaultValue={dayjs()}
                            format="YYYY-MM-DD"
                            style={{ width: '100%' }}
                            popupStyle={{ zIndex: 1300 }}
                        />
                    </div>
                    <div style={{ marginTop: '20px', textAlign: 'right' }}>
                        <Button
                            variant="outlined"
                            onClick={handleMealPlanModalCancel}
                            style={{ marginRight: '10px' }}
                        >
                            Cancel
                        </Button>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleMealPlanFormSubmit}
                        >
                            Submit
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>
        </Box>
    );
}

export default ResultComponent;