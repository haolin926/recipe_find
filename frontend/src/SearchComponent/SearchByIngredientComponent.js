import React, {useEffect, useRef, useState} from "react";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import {Input, Tag} from "antd";
import {PlusOutlined} from "@ant-design/icons";
import PropTypes from "prop-types";

const SearchByIngredientComponent = ({onSearchByIngredients, initialIngredients}) => {

    const [tags, setTags] = useState(initialIngredients || []);
    const [inputVisible, setInputVisible] = useState(false);
    const [inputValue, setInputValue] = useState("");
    const inputRef = useRef(null);

    useEffect(() => {
        if (inputVisible) {
            inputRef.current?.focus();
        }
    }, [inputVisible]);

    const handleClose = (removedTag) => {
        setTags(tags.filter((tag) => tag !== removedTag));
    };

    const showInput = () => {
        setInputVisible(true);
    };

    const handleInputChange = (e) => {
        setInputValue(e.target.value);
    };

    const handleInputConfirm = () => {
        if (inputValue && !tags.includes(inputValue)) {
            setTags([...tags, inputValue]);
        }
        setInputVisible(false);
        setInputValue("");
    };

    const handleSearch = () => {
        onSearchByIngredients(tags);
    };

    return (
        <Box className={"searchIngredientContainer"}>
            <Box className={"tagContainer"}>
                {tags.map((tag) => {
                    return (
                        <Tag
                            key={tag}
                            closable
                            onClose={() => handleClose(tag)}
                            style={{ fontSize: "16px", padding:"8px"}}
                        >
                            {tag}
                        </Tag>
                    );
                })}

                {inputVisible ? (
                    <Input
                        ref={inputRef}
                        type="text"
                        size="small"
                        style={{ width: 120, fontSize: "16px", padding:"10px"}}
                        value={inputValue}
                        onChange={handleInputChange}
                        onBlur={handleInputConfirm}
                        onPressEnter={handleInputConfirm}
                    />
                ) : (
                    <Tag onClick={showInput} style={{ borderStyle: "dashed", fontSize: "16px", padding:"10px"}}>
                        <PlusOutlined /> Add Ingredient
                    </Tag>
                )}
            </Box>
            <Button className="customButton" type="text" size="large" onClick={handleSearch}>Search</Button>
        </Box>
    );
};

SearchByIngredientComponent.propTypes = {
    onSearchByIngredients: PropTypes.func.isRequired,
    initialIngredients: PropTypes.arrayOf(PropTypes.string)
};

export default SearchByIngredientComponent;