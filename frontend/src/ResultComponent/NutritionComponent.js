import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {PieChart} from "@mui/x-charts/PieChart";
import React from "react";
import "./ResultComponent.css";
import "./NutritionComponent.css";
import {Carousel} from "antd";
import PropTypes from 'prop-types';

const NutritionComponent = ({nutrition}) => {
    // if nutrition is undefined or null, set it to an empty array
    const nutritionList = nutrition ?? [];
    let pieData;

    if (nutritionList !== []) {
        pieData = nutritionList.map(nutrition => ({
            label: nutrition.name,
            value: parseFloat(nutrition.amount) // Ensure value is a number
        }));
    }
    return (
            <Box className="bottomContainerPaper">
                <AppBar position="static" sx={{width:"100%", borderRadius: "5px", display:"flex", justifyContent:"center", alignItems:"center"}}>
                    <h1>Nutrient Analysis</h1>
                </AppBar>
                <Box sx={{display:"flex", flexDirection:"row", justifyContent:"space-between", height:"100%", gap:"1%", margin:"1%", overflow:"auto"}}>
                    <Paper elevation={3} sx={{width:"48%", height:"80%", margin:"1%"}}>
                        {nutritionList !== [] && nutritionList.length > 0 ? ( // Ensure nutrition is not null and has items
                        <Carousel dots={{className: "greyDots"}} style={{height:"100%"}}>
                            <div>
                                <div className="carouselItem">
                                <PieChart
                                    series={[
                                        {
                                            data: pieData,
                                            innerRadius: 50,
                                            outerRadius: 100,
                                            paddingAngle: 5,
                                            highlightScope: { fade: 'global', highlight: 'item' },
                                            faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
                                        },
                                    ]}
                                    width={500}
                                    height={300}
                                />
                                </div>
                            </div>
                            <div>
                                <h3>2</h3>
                            </div>
                            <div>
                                <h3>3</h3>
                            </div>
                            <div className={"carouselItem"}>
                                <h3>4</h3>
                            </div>
                        </Carousel>
                            ) : (
                                <h3 style={{textAlign: "center", margin: "auto"}}>No nutrition data available</h3>
                            )}
                    </Paper>
                    <Paper elevation={3} sx={{width:"48%", height:"80%", display:"flex", justifyContent:"center", margin:"1%", overflow:"auto"}}>
                        <TableContainer>
                            <Table stickyHeader aria-label="sticky table">
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Item</TableCell>
                                        <TableCell>Amount</TableCell>
                                        <TableCell>Unit</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    { nutritionList.length > 0 ? ( // Ensure nutrition is not null and has items
                                        nutritionList.map((nutrition, index) => (
                                            <TableRow key={index}>
                                                <TableCell>{nutrition.name}</TableCell>
                                                <TableCell>{nutrition.amount}</TableCell>
                                                <TableCell>{nutrition.unit}</TableCell>
                                            </TableRow>
                                        ))
                                    ) : (
                                        <TableRow>
                                            <TableCell colSpan={3} style={{ textAlign: "center" }}>
                                                No nutrition data available
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Paper>
                </Box>
            </Box>
    );
}


export default NutritionComponent;