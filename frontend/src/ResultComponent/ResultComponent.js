import React from "react";
import {useLocation} from "react-router-dom";
import "./ResultComponent.css";
import Box from "@mui/material/Box";
import {Button, Paper} from "@mui/material";
import { Tabs } from "antd";
import InstructionComponent from "./InstructionComponent";
import TabPane from "antd/es/tabs/TabPane";
import IngredientComponent from "./IngredientComponent";
import NutritionComponent from "./NutritionComponent";
import './ResultComponent.css';

const ResultComponent = () => {
    const location = useLocation();
    const {item} = location.state;

    if (!item) {
        return <div>No data found</div>;
    }

    const {name, image, instructions, ingredients, nutrition} = item;


    return (
        <Box sx={{display:"flex", width:"100%", height:"100vh", flexDirection:"column"}}>
            <Box sx={{display:"flex", width:"98%", height:"40%", margin:"1%"}}>
                <Paper elevation={2} sx={{width:"100%", height:"100%", display:"flex", flexDirection:"row", padding:"20px"}}>
                    <Box sx={{display:"flex", flexDirection:"column", justifyContent:"space-between"}}>
                        <h1>{name}</h1>
                        <Box sx={{ display: "flex", flexDirection: "column", gap: "20px" }}>
                            <Button variant="contained" size="large" sx={{width:"400px", height:"50px"}}>
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
                            <InstructionComponent instructions={instructions}/>
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



            {/*<div id={"content"}>*/}
            {/*    <div id={"upper_content"}>*/}
            {/*        <div id={"dish"}>*/}
            {/*            <div id={"recipe_name"}>*/}
            {/*                {name}*/}
            {/*            </div>*/}
            {/*            <div>*/}
            {/*                <img src={image} alt={"Recipe"} id={"recipe_image"}/>*/}
            {/*            </div>*/}
            {/*        </div>*/}
            {/*        <div id={"instruction_list"}>*/}
            {/*            <h2>Instructions</h2>*/}
            {/*            <ol id={"steps"}>*/}
            {/*                {instructions.map((instruction, index) =>*/}
            {/*                    <li key={index}>{instruction}</li>)}*/}
            {/*            </ol>*/}
            {/*        </div>*/}
            {/*    </div>*/}
            {/*    <div id={"lower_content"}>*/}
            {/*        <div id={"ingredient_list"} className={"info_list"}>*/}
            {/*            <h2>Ingredients</h2>*/}
            {/*            <ul>*/}
            {/*                {ingredients.map((ingredient, index) => (*/}
            {/*                    <li key={index}>{ingredient.ingredient}: {ingredient.amount_unit}</li>*/}
            {/*                ))}*/}
            {/*            </ul>*/}
            {/*        </div>*/}

            {/*        <div id={"nutrition_list"} className={"info_list"}>*/}
            {/*            <h2>Nutrition</h2>*/}
            {/*            <ul>*/}
            {/*                {nutrition.map((nutrient, index) => (*/}
            {/*                    <li key={index}>{nutrient.nutrient}: {nutrient.amount_unit}</li>*/}
            {/*                ))}*/}
            {/*            </ul>*/}
            {/*        </div>*/}
            {/*    </div>*/}
            {/*</div>*/}
        </Box>
    );
}

export default ResultComponent;