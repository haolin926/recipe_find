import {Button, styled} from "@mui/material";
import React from "react";
import Box from "@mui/material/Box";
import PropTypes from "prop-types";
import {UploadOutlined} from "@ant-design/icons";

const SearchByImageComponent = ({ onImageUpload }) => {

    const VisuallyHiddenInput = styled('input')({
        clip: 'rect(0 0 0 0)',
        clipPath: 'inset(50%)',
        height: 1,
        overflow: 'hidden',
        position: 'absolute',
        bottom: 0,
        left: 0,
        whiteSpace: 'nowrap',
        width: 1,
    });

    return (
        <Box className={"searchBarContainer"}>
            <Button
                component="label"
                role={undefined}
                variant="contained"
                tabIndex={-1}
                startIcon={<UploadOutlined />}
                className={"customButton"}
                size={"large"}
            >
                Upload Dish Image
                <VisuallyHiddenInput
                    type="file"
                    accept="image/*"
                    onChange={onImageUpload}
                    multiple
                />
            </Button>
        </Box>
    );
};

SearchByImageComponent.propTypes = {
    onImageUpload: PropTypes.func.isRequired
};

export default SearchByImageComponent;