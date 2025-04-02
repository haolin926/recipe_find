import React, {useEffect, useMemo, useState} from "react";
import { Box, AppBar, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@mui/material";
import "./IngredientComponent.css";
import PropTypes from "prop-types";

const IngredientComponent = ({ ingredients, limitHeight }) => {
    const ingredientList = useMemo(() => ingredients ?? [], [ingredients]); // Ensure ingredients is an array

    const [rows, setRows] = useState([]);

    useEffect(() => {
        const newRows = ingredientList.map((ingredient, index) => ({
            id: index,
            name: ingredient?.name ?? "Unknown Ingredient",
            amount: ingredient?.amount ?? "N/A",
            selected: false,
            unit: ingredient?.unit ?? ""
        }));

        // Only update state if the new data is different
        setRows(prevRows => (JSON.stringify(prevRows) === JSON.stringify(newRows) ? prevRows : newRows));
    }, [ingredientList]);

    return (
        <Box className={`bottomContainerPaper ${limitHeight ? 'limitedHeight' : ''}`} sx={{position:"relative"}}>
            <Box sx={{ width: "100%" }}>
                <AppBar position="static" className={"commonHeader"}>
                    <h1>Ingredient Summary</h1>
                </AppBar>
            </Box>
                <Box className={"resultInfoContainer"}>
                <TableContainer component={Box} sx={{ height: "100%", overflow: "auto" }}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell className={"tableHeadCell"}>Ingredient</TableCell>
                                <TableCell className={"tableHeadCell"}>Amount</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.length > 0 ? (
                                rows.map((row) => (
                                    <TableRow key={row.id}>
                                        <TableCell>{row.name}</TableCell>
                                        <TableCell>{`${row.amount} ${row.unit}`}</TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={3} align="center">
                                        No ingredients available
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>
        </Box>
    );
};

IngredientComponent.propTypes = {
    ingredients: PropTypes.array.isRequired,
    limitHeight: PropTypes.bool
};

export default IngredientComponent;