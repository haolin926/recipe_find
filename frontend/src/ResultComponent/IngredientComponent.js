import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import {Checkbox, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import React, {useState} from "react";
import "./ResultComponent.css";
import {FloatButton} from "antd";
import {PlusOutlined} from "@ant-design/icons";

const IngredientComponent = (ingredients) => {

    const [rows, setRows] = useState(
        ingredients.ingredients.map((ingredient, index) => ({
            id: index,
            name: ingredient.name,
            amount: ingredient.amount,
            selected: false,
        }))
    );
    const handleCheckboxChange = (id) => {
        setRows((prevRows) =>
            prevRows.map((row) =>
                row.id === id ? { ...row, selected: !row.selected } : row
            )
        );
    };

    const handleSelectAll = (event) => {
        const isChecked = event.target.checked;
        setRows((prevRows) =>
            prevRows.map((row) => ({ ...row, selected: isChecked }))
        );
    };

    const allSelected = rows.every((row) => row.selected);
    return (
        <Box className="bottomContainerPaper">
            <Box sx={{width:"100%"}}>
                <AppBar position="static" sx={{width:"100%", borderRadius: "5px", display:"flex", justifyContent:"center", alignItems:"center"}}>
                    <h1>Ingredient Summary</h1>
                </AppBar>
            </Box>
            <TableContainer component={Box} sx={{height:"100%", overflow:"auto"}}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell padding="checkbox">
                                <Checkbox
                                    checked={allSelected}
                                    onChange={handleSelectAll}
                                />
                            </TableCell>
                            <TableCell>Ingredient</TableCell>
                            <TableCell>Amount</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map((row) => (
                            <TableRow key={row.id}>
                                <TableCell padding="checkbox">
                                    <Checkbox
                                        checked={row.selected}
                                        onChange={() => handleCheckboxChange(row.id)}
                                    />
                                </TableCell>
                                <TableCell>{row.name}</TableCell>
                                <TableCell>{row.amount}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <FloatButton
                style={{position:"absolute", color:"white", backgroundColor:"#e67e22", bottom:"10px", right:"10px"}}
                tooltip={<div>Add Selected Ingredient To Shopping List</div>}
                icon={<PlusOutlined/>}
                type={"primary"}
                ></FloatButton>
        </Box>
    )
}

export default IngredientComponent;