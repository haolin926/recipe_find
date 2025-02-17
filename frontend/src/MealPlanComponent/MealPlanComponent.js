import * as React from 'react';
import dayjs from 'dayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import Box from "@mui/material/Box";
import {
    Button,
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



export default function MealPlanComponent() {

    const [selectedDate, handleDateChange] = React.useState(dayjs('2025-01-01'));

    return (
        <Box sx={{margin:"1%", overflow:"auto"}}>
            <Box sx={{display:"flex", width:"98%", height:"100px", alignItems:"center", justifyContent:"space-between"}}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <DatePicker
                        value={selectedDate}
                        onChange={(newValue) => handleDateChange(newValue)}
                    />
                </LocalizationProvider>
                <Button variant="contained">
                    Add New Recipe
                </Button>
            </Box>
            <Box>
                <Button variant="outlined">
                    Show Weekly Summary
                </Button>
            </Box>
            <Box sx={{display:"flex", justifyContent:"space-between", width:"100%", height:"500px", marginTop:"20px"}}>
                <Box className="bottomContainerPaper" sx={{width:"49%"}}>
                    <Box sx={{width:"100%"}}>
                        <AppBar position="static" sx={{width:"100%", borderRadius: "5px", display:"flex", justifyContent:"center", alignItems:"center"}}>
                            <h1>Saved Recipes</h1>
                        </AppBar>
                    </Box>
                    <Paper sx={{height:"100%", overflow:"auto"}}>
                        <List>
                            <ListItem
                                secondaryAction={
                                    <IconButton edge="end" aria-label="delete">
                                        <DeleteOutlined />
                                    </IconButton>}>
                                <ListItemButton>
                                    <ListItemAvatar>
                                    </ListItemAvatar>
                                    <ListItemText>
                                        <h3>Recipe name</h3>
                                    </ListItemText>
                                </ListItemButton>
                            </ListItem>
                            <ListItem
                                secondaryAction={
                                    <IconButton edge="end" aria-label="delete">
                                        <DeleteOutlined />
                                    </IconButton>}>
                                <ListItemButton>
                                    <ListItemAvatar>
                                    </ListItemAvatar>
                                    <ListItemText>
                                        <h3>Recipe name</h3>
                                    </ListItemText>
                                </ListItemButton>
                            </ListItem>
                            <ListItem
                                secondaryAction={
                                    <IconButton edge="end" aria-label="delete">
                                        <DeleteOutlined />
                                    </IconButton>}>
                                <ListItemButton>
                                    <ListItemAvatar>
                                    </ListItemAvatar>
                                    <ListItemText>
                                        <h3>Recipe name</h3>
                                    </ListItemText>
                                </ListItemButton>
                            </ListItem>
                            <ListItem
                                secondaryAction={
                                    <IconButton edge="end" aria-label="delete">
                                        <DeleteOutlined />
                                    </IconButton>}>
                                <ListItemButton>
                                    <ListItemAvatar>
                                    </ListItemAvatar>
                                    <ListItemText>
                                        <h3>Recipe name</h3>
                                    </ListItemText>
                                </ListItemButton>
                            </ListItem>
                            <ListItem
                                secondaryAction={
                                    <IconButton edge="end" aria-label="delete">
                                        <DeleteOutlined />
                                    </IconButton>}>
                                <ListItemButton>
                                    <ListItemAvatar>
                                    </ListItemAvatar>
                                    <ListItemText>
                                        <h3>Recipe name</h3>
                                    </ListItemText>
                                </ListItemButton>
                            </ListItem>
                        </List>
                    </Paper>
                </Box>

                <Box sx={{width:"49%", height:"100%", display:"flex", flexDirection:"column"}}>
                    <Paper>
                        <IngredientComponent/>
                    </Paper>
                </Box>
            </Box>
            <Paper sx={{width:"100%", marginTop:"0"}}>
                <NutritionComponent/>
            </Paper>
        </Box>
    );
}