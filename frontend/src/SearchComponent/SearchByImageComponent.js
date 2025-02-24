import {Button, styled} from "@mui/material";
import React from "react";
import Box from "@mui/material/Box";
import axios from "axios";

const SearchByImageComponent = ({ onImageUpload }) => {

    const [image, setImage] = React.useState(null); // Store the image
    const [uploading, setUploading] = React.useState(false); // Track uploading state

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


    const handleImageChange = async (event) => {
        const file = event.target.files[0];

        if (file) {
            setImage(file);
            setUploading(true);

            try {
                const formData = new FormData();
                formData.append("image", file);

                const response = await axios.post("http://localhost:8080/api/recipe/image", formData, {
                    headers: {
                        "Content-Type": "multipart/form-data", // Specify content type for file upload
                    },
                });
                console.log(response.data);
                onImageUpload(response.data, file); // Pass the response data to the parent
                setUploading(false); // Reset uploading state

            } catch (error) {
                console.error("error uploading Image");
                setUploading(false);
            }
        }
    };

    return (
        <Box className={"searchBarContainer"}>
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
                    onChange={handleImageChange}
                    multiple
                />
            </Button>
        </Box>
    );
};

export default SearchByImageComponent;