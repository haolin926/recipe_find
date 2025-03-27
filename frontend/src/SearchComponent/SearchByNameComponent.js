import React, { useState } from "react";
import { TextField } from "@mui/material";
import Button from "@mui/material/Button";
import "./SearchComponent.css";
import Box from "@mui/material/Box";
import './SearchByNameComponent.css';
import PropTypes from "prop-types";

const SearchByNameComponent = ({onSearchByName}) => {

    const [recipeName, setRecipeName] = useState("");

    const handleInputChange = (event) => {
        setRecipeName(event.target.value);
    };

    const handleSubmit = async () => {
        onSearchByName(recipeName);
    };
    return (
            <Box className={"searchBarContainer"}>
                <TextField
                    id={"userInputNameField"}
                    label="Enter Recipe Name"
                    variant="filled"
                    className="customTextField"

                    value={recipeName}
                    onChange={handleInputChange}
                />
                <Button className="customButton" type="text" size="large" onClick={handleSubmit}>Submit</Button>
            </Box>
    );
};

SearchByNameComponent.PropTypes = {
    onSearchByName: PropTypes.func.isRequired
};

export default SearchByNameComponent;