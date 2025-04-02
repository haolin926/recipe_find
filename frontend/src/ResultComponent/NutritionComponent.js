import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {PieChart} from "@mui/x-charts/PieChart";
import React from "react";
import "./ResultComponent.css";
import "./NutritionComponent.css";
import PropTypes from "prop-types";

const NutritionComponent = ({nutrition}) => {
    // if nutrition is undefined or null, set it to an empty array
    const nutritionList = nutrition ?? [];
    let pieData;

    if (nutritionList.length > 0) {
        pieData = nutritionList.map(nutrition => ({
            label: nutrition.name,
            value: parseFloat(nutrition.amount) // Ensure value is a number
        }));
    }
    return (
            <Box className="bottomContainerPaper">
                <Box>
                    <AppBar position="static" className={"commonHeader"}>
                        <h1>Nutrient Analysis</h1>
                    </AppBar>
                </Box>
                <Box className={"resultInfoContainer nutritionPaperContainer"} >
                    <Paper elevation={3} className={"nutritionPaper"}>
                        {nutritionList.length > 0 ? (
                            <div>
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
                                        sx={{margin:"20px"}}
                                    />
                                    </div>
                                </div>
                            </div>
                            ) : (
                                <h3 style={{textAlign: "center", margin: "auto"}}>No nutrition data available</h3>
                            )}
                    </Paper>
                    <Paper elevation={3} className={"nutritionPaper nutritionInfo"}>
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
                                            <TableRow key={`${nutrition.name}-${index}`}>
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

NutritionComponent.propTypes = {
    nutrition: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string.isRequired,
            amount: PropTypes.string.isRequired,
            unit: PropTypes.string
        })
    ).isRequired
};


export default NutritionComponent;