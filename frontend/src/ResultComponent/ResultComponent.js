import React, {useContext, useState} from "react";
import {useLocation} from "react-router-dom";
import "./ResultComponent.css";
import Box from "@mui/material/Box";
import {Button, Dialog, DialogContent, DialogTitle, Paper, useMediaQuery} from "@mui/material";
import {message, Modal, Tabs} from "antd";
import InstructionComponent from "./InstructionComponent";
import TabPane from "antd/es/tabs/TabPane";
import IngredientComponent from "./IngredientComponent";
import NutritionComponent from "./NutritionComponent";
import {AuthContext} from "../AuthContext";
import axios from "axios";
import LoginWindowComponent from "../LoginWindowComponent/LoginWindowComponent";

const ResultComponent = () => {
    const location = useLocation();
    const {recipe} = location.state;
    const {user} = useContext(AuthContext);
    const [isLoginModalVisible, setLoginModalVisible] = useState(false);

    const handleCancel = () => {
        setLoginModalVisible(false);
    };


    const {name, image, instructions, ingredients, nutrition} = recipe;
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
                    console.error("Failed to add recipe to favorites");
                }
            } catch (error) {
                console.log("error occurred when adding to favourite");
            }
        } else {
            message.info("Login Required")
            setLoginModalVisible(true);
        }
    }

    return (
        <Box sx={{display:"flex", width:"100%", height:"100vh", flexDirection:"column"}}>
            <Box sx={{display:"flex", width:"98%", height:"40%", margin:"1%"}}>
                <Paper elevation={2} sx={{width:"100%", height:"100%", display:"flex", flexDirection:"row", padding:"20px"}}>
                    <Box sx={{display:"flex", flexDirection:"column", justifyContent:"space-between"}}>
                        <h1>{name}</h1>
                        <Box sx={{ display: "flex", flexDirection: "column", gap: "20px" }}>
                            <Button variant="contained" size="large" sx={{width:"400px", height:"50px"}}
                                    onClick={handleAddToFavorites}>
                                Add Recipe to Favorites
                            </Button>
                            <Button variant="contained" size="large" sx={{ width:"400px", height:"50px"}}>
                                Add Recipe to Meal Plan
                            </Button>
                        </Box>
                    </Box>
                    <Box sx={{marginLeft:"auto", height:"100%", width:"25%"}}>
                        <Paper sx={{width: "100%", height:"100%"}}>
                            <img src={image} alt={"recognize"} style={{width:"100%", height:"100%", objectFit:"cover"}}/>
                        </Paper>
                    </Box>
                </Paper>
            </Box>

            <Box sx={{display:"flex", width:"98%", height:"80%", margin:"1%", marginTop:"2%"}}>
                <Paper sx={{display:"flex", flexDirection: "column", width:"100%", marginTop:"1%", overflow:"auto"}}>
                    <Tabs defaultActiveKey="1" tabPosition="bottom" centered className="customTabs">
                        <TabPane tab="Instructions" key="1">
                            <InstructionComponent instructions={instructions || []}/>
                        </TabPane>
                        <TabPane tab="Ingredients" key="2">
                            <IngredientComponent ingredients={ingredients}/>
                        </TabPane>
                        <TabPane tab="Nutrition" key="3">
                            <NutritionComponent nutrition={nutrition}/>
                        </TabPane>
                    </Tabs>
                </Paper>
            </Box>

            <Dialog
                open={isLoginModalVisible}
                onClose={handleCancel}
                centered
            >
                <DialogContent sx={{ width: '500px', padding:0 }}>
                    <LoginWindowComponent />
                </DialogContent>
            </Dialog>
        </Box>
    );
}

export default ResultComponent;