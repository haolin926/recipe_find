import Box from "@mui/material/Box";
import * as React from "react";
import {Card, message, Space} from "antd";
import {DeleteOutlined, EditOutlined} from "@ant-design/icons";
import "./SavedRecipeComponent.css";
import axios from "axios";
import {useContext, useEffect, useState} from "react";
import {AuthContext} from "../AuthContext";
import {useNavigate} from "react-router-dom";

function SavedRecipeComponent () {
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();
    const [recipes, setRecipes] = useState([]);
    const fetchSavedRecipe = async () => {
        if (user == null) {
            message.info("You must login first to see saved recipes");
            navigate("/login")
            return;
        }
        try {
            const response = await axios.get(
                "http://localhost:8080/api/savedRecipe",
                {
                    params :{userId : user.id},
                    withCredentials: true
                }
            );
            if (response.status === 200) {
                console.log(response.data);
                setRecipes(response.data);
            } else {
                console.error('Failed to fetch saved recipes:', response.statusText);
                setRecipes([]);
            }
        } catch (error) {
            console.error('Error fetching saved recipes:', error);
            setRecipes([]);
        }
    }

    const deleteSavedRecipe = async (recipe) => {
        try {
            const response = await axios.delete(
                "http://localhost:8080/api/savedRecipe/delete",
                {
                    params: {userId: user.id, recipeId: recipe.id},
                    withCredentials: true
                }
            );
            if (response.status === 200) {
                setRecipes(prevRecipes => prevRecipes.filter(r => r.id !== recipe.id));
                message.success("Deleted Successfully");
            }
        } catch (error) {
                message.error("Error when deleting recipe");
                console.error("Error when deleting recipe:", error)
        }
    }

    const { Meta } = Card;
    const handleImageClick = (recipe) => {
        navigate('/result', { state: { recipe } });
    };

    useEffect(() => {
        if (user) {
            // Call fetchSavedRecipe only if user is logged in
            fetchSavedRecipe();
        }
    }, [user]);

    const truncateDescription = (description) => {
        if (description) {
            const words = description.split(" ");
            return words.length > 10 ? words.slice(0, 10).join(" ") + "..." : description;
        } else {
            return "No Description provided";
        }
    };

    return (
        <Box sx={{height: "100%", display:"flex", marginTop:"3%", flexDirection:"column", alignItems:"center", minHeight:"100vh", paddingBottom:"5%"}}>
            <Space wrap size="large" style={{ display: "flex", width:"80%", alignItems:"center"}}>
                {recipes.map((recipe, index) => (
                    <Card
                        hoverable
                        style={{
                            width: 240,
                        }}
                        cover={
                        <div className={"cardImageContainer"}>
                            <img className="cardImage" alt="example" src={recipe.image} onClick={() => handleImageClick(recipe)} />
                        </div>
                        }
                        actions={[
                            <EditOutlined onClick={() => handleImageClick(recipe)}></EditOutlined>,
                            <DeleteOutlined onClick ={() => deleteSavedRecipe(recipe)}></DeleteOutlined>
                        ]}
                    >
                        <Meta
                            title={recipe.name}
                            description={
                                <div className="cardDescription">
                                    {truncateDescription(
                                        recipe.description
                                    )}
                                </div>
                            }
                            onClick={() => handleImageClick(recipe)}
                        />
                    </Card>
                ))}
            </Space>
        </Box>
    );
}

export default SavedRecipeComponent;