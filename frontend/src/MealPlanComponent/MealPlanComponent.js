import * as React from 'react';
import dayjs from 'dayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import Box from "@mui/material/Box";
import {
    Button, Dialog, DialogActions, DialogContent, DialogTitle,
    IconButton,
    ListItem,
    ListItemAvatar,
    ListItemButton,
    Paper
} from "@mui/material";
import AppBar from "@mui/material/AppBar";
import List from "@mui/material/List";
import ListItemText from "@mui/material/ListItemText";
import {DeleteOutlined} from "@ant-design/icons";
import "../ResultComponent/ResultComponent.css";
import IngredientComponent from "../ResultComponent/IngredientComponent";
import NutritionComponent from "../ResultComponent/NutritionComponent";
import {useCallback, useContext, useEffect, useState} from "react";
import {AuthContext} from "../AuthContext";
import {useNavigate} from "react-router-dom";
import {Avatar, message} from "antd";
import axios from "axios";
import "./MealPlanComponent.css";



export default function MealPlanComponent() {

    const [selectedDate, setSelectedDate] = useState(() => dayjs());
    const { user, loading } = useContext(AuthContext);
    const [MealPlan, setMealPlan] = useState([]);
    const [weeklySummary, setWeeklySummary] = useState(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const navigate = useNavigate();

    const fetchMealPlanForCurrentDate = useCallback(async () => {
        if (user == null) {
            message.info("You must login first to visit meal plan");
            navigate("/login");
            return;
        }
        try {
            const formattedDate = selectedDate.format("YYYY-MM-DD");
            const response = await axios.get(
                "http://localhost:8080/api/mealplan/ondate",
                {
                    params: { userId: user.id, date: formattedDate },
                    withCredentials: true
                }
            );
            if (response.status === 200) {
                console.log(response.data);
                setMealPlan(response.data);
            } else {
                console.error('Failed to fetch meal plan:', response.statusText);
                setMealPlan([]);
            }
        } catch (error) {
            console.error('Error fetching meal plan:', error);
            setMealPlan([]);
        }
    }, [user, selectedDate, navigate]);
    
    useEffect(() => {
        if (loading) return;

        if (user) {
            // Call fetchSavedRecipe only if user is logged in
            fetchMealPlanForCurrentDate();
        } else {
            message.info("You must login first to visit meal plan");
            navigate("/login");
        }
    }, [user, selectedDate, fetchMealPlanForCurrentDate, navigate]);

    const getWeeklySummary = async () => {
        try {
            const formattedDate = selectedDate.format("YYYY-MM-DD");
            const response = await axios.get(
                "http://localhost:8080/api/mealplan/getWeekSummary",
                {
                    params :{userId: user.id, date: formattedDate},
                    withCredentials: true
                }
            );
            if (response.status === 200) {
                console.log(response.data);
                setWeeklySummary(response.data);
                setDialogOpen(true);
            } else {
                console.error('Failed to fetch meal plan:', response.statusText);
            }
        } catch (error) {
            console.error("Failed to fetch weekly summary");
        }
    }
    const handleCloseDialog = () => {
        setDialogOpen(false);
    };

    const handleDelete = async (mealplanId, recipeId) => {
        try {
            const response = await axios.delete("http://localhost:8080/api/mealplan/remove", {
                params: { mealPlanId: mealplanId, recipeId: recipeId },
                withCredentials: true // If authentication is needed
            });

            if (response.status === 200) {
                message.success("Recipe successfully deleted from meal plan");
                await fetchMealPlanForCurrentDate();
            } else {
                message.error("Failed to delete recipe from meal plan");
            }
        } catch (error) {
            console.error("Error deleting recipe from meal plan:", error);
            message.error("Failed to delete recipe from meal plan");
        }
    };

    function handleClick(recipe) {
        navigate('/result', {state: {recipeId: recipe.recipeApiId}});
    }

    return (
        <Box className={"mealPlanBody"}>
            <Box className={"mealPlanTopContainer"}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <DatePicker
                        value={selectedDate}
                        onChange={(newValue) => setSelectedDate(newValue)}
                    />
                </LocalizationProvider>
            </Box>
            <Box className={"showWeeklySummaryButtonContainer"}>
                <Button variant="outlined" onClick={getWeeklySummary}>
                    Show Weekly Summary
                </Button>
            </Box>
            <Box className={"plannedRecipeAndIngredient"}>
                <Box className="mealPlanInfoContainer">
                    <Box>
                        <AppBar position="static" className={"commonHeader"}>
                            <h1>Planned Recipes</h1>
                        </AppBar>
                    </Box>
                    <Paper className={"savedRecipePaper"}>
                        <List>
                            {(MealPlan.recipeDTOList || []).map((recipe, index) => (
                                <ListItem key={recipe.recipeId || index} secondaryAction={
                                    <IconButton edge="end" aria-label="delete" onClick={() => handleDelete(MealPlan.id, recipe.id)}>
                                        <DeleteOutlined />
                                    </IconButton>
                                }>
                                    <ListItemButton onClick={() => handleClick(recipe)}>
                                        <ListItemAvatar>
                                            <div className={"imageContainer"}>
                                                <Avatar className = "searchListImage" src={recipe.image}  alt={"searchresult"}/>
                                            </div>
                                        </ListItemAvatar>
                                        <ListItemText
                                            primary={<h3>{recipe.name}</h3>} // Recipe name
                                            secondary={<span>{recipe.description || "No description available"}</span>} // Optional description
                                            style = {{marginLeft:"20px"}}
                                        />
                                    </ListItemButton>
                                </ListItem>
                            ))}
                        </List>
                    </Paper>
                </Box>

                <Box className={"mealPlanInfoContainer"}>
                    <Paper className={"mealPlanIngredientPaper"}>
                        <IngredientComponent ingredients={MealPlan.ingredientDTOList} limitHeight={true}/>
                    </Paper>
                </Box>
            </Box>
            <Paper sx={{width:"100%", marginTop:"20px"}}>
                <NutritionComponent nutrition={MealPlan.nutritionDTOList}/>
            </Paper>

            <Dialog open={dialogOpen} onClose={handleCloseDialog} fullWidth maxWidth="md">
                <DialogTitle>Weekly Summary</DialogTitle>
                <DialogContent>
                    {weeklySummary && (
                        <>
                            <h2>Ingredients</h2>
                            <List>
                                {weeklySummary.ingredientDTOList.map((ingredient, index) => (
                                    <ListItem key={`${ingredient.name}-${index}`}>
                                        <ListItemText primary={ingredient.name} secondary={`${ingredient.amount} ${ingredient.unit}`} />
                                    </ListItem>
                                ))}
                            </List>
                            <h2>Nutrition</h2>
                            <List>
                                {weeklySummary.nutritionDTOList.map((nutrition, index) => (
                                    <ListItem key={`${nutrition.name}-${index}`}>
                                        <ListItemText primary={nutrition.name} secondary={`${nutrition.amount} ${nutrition.unit}`} />
                                    </ListItem>
                                ))}
                            </List>
                        </>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} color="primary">
                        Close
                    </Button>
                </DialogActions>

            </Dialog>
        </Box>
    );
}