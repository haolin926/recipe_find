import {Button, styled} from "@mui/material";
import React from "react";
import Box from "@mui/material/Box";

const SearchByImageComponent = ({onBack}) => {

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
        <Box
            sx={{
                display: "flex",
                flexDirection: "column",
                alignItems: "flex-start",
                gap: "16px",
                width:"100%",
                margin:"auto"
            }}>
            <Box
                sx={{
                    display: "flex",
                    width: "100%",
                    flexDirection: "row",
                    alignItems: "center",
                    justifyContent:"center",
                }}>
                <Button
                    component="label"
                    role={undefined}
                    variant="contained"
                    tabIndex={-1}
                    className={"customButton"}
                    size={"large"}
                    sx={{
                        width: "30%"
                    }}
                >
                    Upload Dish Image
                    <VisuallyHiddenInput
                        type="file"
                        accept="image/*"
                        onChange={(event) => console.log(event.target.files)}
                        multiple
                    />
                </Button>
            </Box>
        </Box>
    );
};

export default SearchByImageComponent;