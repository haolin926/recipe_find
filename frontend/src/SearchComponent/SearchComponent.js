import Box from "@mui/material/Box";
import React from "react";
import {Paper, Tab} from "@mui/material";
import {TabContext, TabList, TabPanel} from "@mui/lab";
import Tabs from '@mui/material/Tabs';
import AppBar from "@mui/material/AppBar";
import SearchByNameComponent from "./SearchByNameComponent";
import SearchByImageComponent from "./SearchByImageComponent";
import {EditOutlined, FileImageOutlined, FireOutlined} from "@ant-design/icons";
import {useLocation} from "react-router-dom";

function SearchComponent () {

    const location = useLocation();
    const [value, setValue] = React.useState(location.state?.value || "1");

    const handleChange = (event, newValue) => {
        setValue(newValue);
    };

    return (
        <Box className={"bodyBackground"} sx={{display:"flex", justifyContent:"center"}}>
            <Paper sx={{width:"50%", margin:"3%", height:"20%"}}>
                <TabContext value={value}>
                    <AppBar position={"static"}>
                        <Tabs value={value} onChange={handleChange} centered variant="fullWidth" textColor="inherit">
                            <Tab label="Search By Name" value="1" icon={<EditOutlined />}/>
                            <Tab label="Search By Image" value="2" icon={<FileImageOutlined />}/>
                            <Tab label="Search By Ingredients" value="3"icon={<FireOutlined />}/>
                        </Tabs>
                    </AppBar>
                    <TabPanel value="1">
                        <SearchByNameComponent/>
                    </TabPanel>
                    <TabPanel value="2">
                        <SearchByImageComponent/>
                    </TabPanel>
                    <TabPanel value="3">
                        content 3
                    </TabPanel>
                </TabContext>
            </Paper>
        </Box>
    );
}

export default SearchComponent;