import Box from "@mui/material/Box";
import React from "react";
import {Paper, Tab} from "@mui/material";
import {TabContext, TabPanel} from "@mui/lab";
import Tabs from '@mui/material/Tabs';
import AppBar from "@mui/material/AppBar";
import SearchByNameComponent from "./SearchByNameComponent";
import SearchByImageComponent from "./SearchByImageComponent";
import {EditOutlined, FileImageOutlined, FireOutlined} from "@ant-design/icons";
import {useLocation} from "react-router-dom";
import RecognitionResultComponent from "./RecognitionResultComponent/RecognitionResultComponent";
import ResultListComponent from "./ResultListComponent";
import axios from "axios";
function SearchComponent () {

    const location = useLocation();
    const [value, setValue] = React.useState(location.state?.value || "1");
    const [contentToDisplay, setContentToDisplay] = React.useState(null);
    const [predictionResult, setPredictionResult] = React.useState(null);
    const [uploadedImage, setUploadedImage] = React.useState(null);
    const [searchResult, setSearchResult] = React.useState(null);

    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    const handleSearchInput = (data) => {
        setContentToDisplay("searchResult");
        setSearchResult(data);
    }

    const handleImageUpload = ( data, image ) => {
        setContentToDisplay("predictResult"); // Update the state when image is uploaded
        setPredictionResult(data);
        setUploadedImage(image);
    };

    const handleSearchByName = async (name) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/recipe/name?queryName=${name}`);
            console.log(response.data);
            handleSearchInput(response.data);
        } catch (error) {
            console.error("Error fetching recipe by name:", error);
        }
    }

    return (
        <Box className={"bodyBackground"} sx={{display:"flex", alignItems:"center", flexDirection:"column"}}>
            <Paper className="contentContaienr" sx={{width:"50%", margin:"3%", height:"20%"}}>
                <TabContext value={value}>
                    <AppBar position={"static"}>
                        <Tabs value={value} onChange={handleChange} centered variant="fullWidth" textColor="inherit">
                            <Tab label="Search By Name" value="1" icon={<EditOutlined />}/>
                            <Tab label="Search By Image" value="2" icon={<FileImageOutlined />}/>
                            <Tab label="Search By Ingredients" value="3" icon={<FireOutlined />}/>
                        </Tabs>
                    </AppBar>
                    <TabPanel value="1">
                        <SearchByNameComponent onSearchByName={handleSearchByName}/>
                    </TabPanel>
                    <TabPanel value="2">
                        <SearchByImageComponent onImageUpload={handleImageUpload}/>
                    </TabPanel>
                    <TabPanel value="3">
                        content 3
                    </TabPanel>
                </TabContext>
            </Paper>

            <Paper className={"contentContainer"}>
                {contentToDisplay === 'searchResult' && <ResultListComponent searchResult={searchResult}/>}
                {contentToDisplay === 'predictResult' && <RecognitionResultComponent predictions={predictionResult} image={uploadedImage} onSearchByName={handleSearchByName}/>}
            </Paper>
        </Box>
    );
}

export default SearchComponent;