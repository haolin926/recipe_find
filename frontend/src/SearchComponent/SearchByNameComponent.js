import React, { useState } from "react";
import { Container, TextField } from "@mui/material";
import Button from "@mui/material/Button";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import Box from "@mui/material/Box";
import './SearchByNameComponent.css';

const SearchByNameComponent = ({onBack}) => {

    return (
        <Box
            sx={{
                display: "flex",
                flexDirection: "column",
                alignItems: "flex-start",
                gap: "16px",
                paddingTop: "16px",
                paddingLeft: "30px",
                width:"100%"
            }}>
            <Box
                sx={{
                    display: "flex",
                    width: "100%",
                    flexDirection: "row",
                    alignItems: "center",
                    justifyContent:"center",
                    gap: "16px",
                }}>
                <TextField
                    label="Enter Recipe Name"
                    variant="filled"
                    className="customTextField"
                    sx={{
                        width: "30%",
                        color:"#e67e22",
                        backgroundColor:"white",
                    }}
                />
                <Button className="customButton" type="text" size="large">Submit</Button>
            </Box>
        </Box>
    );
};

export default SearchByNameComponent;