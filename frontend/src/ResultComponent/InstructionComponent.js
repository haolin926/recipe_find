import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import List from "@mui/material/List";
import {Accordion, AccordionDetails, AccordionSummary, ListItem} from "@mui/material";
import {DownOutlined} from "@ant-design/icons";
import Typography from "@mui/material/Typography";
import React from "react";
import "./ResultComponent.css";
const ResultComponent = () => {

    return (
        <Box className="bottomContainerPaper">
            <AppBar position="static" sx={{width:"100%", borderRadius: "5px", display:"flex", justifyContent:"center", alignItems:"center"}}>
                <h1>Instructions</h1>
            </AppBar>
            <Box sx={{
                flex: 1,
                overflow: "auto", // Enable vertical scrolling
                height:"100%",
                width:"100%",
            }}>
                <List
                    sx={{
                        position: 'static',
                        overflow: 'auto',
                        width: '80%',
                        margin: 'auto',
                    }}>
                    <ListItem>
                        <Accordion sx={{width:"100%"}}>
                            <AccordionSummary
                                expandIcon={<DownOutlined />}
                                aria-controls="panel1-content"
                                id="panel1-header"
                            >
                                <Typography component="span">Step 1</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
                                malesuada lacus ex, sit amet blandit leo lobortis eget.
                            </AccordionDetails>
                        </Accordion>
                    </ListItem>
                    <ListItem>
                        <Accordion>
                            <AccordionSummary
                                expandIcon={<DownOutlined />}
                                aria-controls="panel1-content"
                                id="panel1-header"
                            >
                                <Typography component="span">Step 1</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
                                malesuada lacus ex, sit amet blandit leo lobortis eget.
                            </AccordionDetails>
                        </Accordion>
                    </ListItem>
                    <ListItem>
                        <Accordion>
                            <AccordionSummary
                                expandIcon={<DownOutlined />}
                                aria-controls="panel1-content"
                                id="panel1-header"
                            >
                                <Typography component="span">Step 1</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
                                malesuada lacus ex, sit amet blandit leo lobortis eget.
                            </AccordionDetails>
                        </Accordion>
                    </ListItem>
                    <ListItem>
                        <Accordion>
                            <AccordionSummary
                                expandIcon={<DownOutlined />}
                                aria-controls="panel1-content"
                                id="panel1-header"
                            >
                                <Typography component="span">Step 1</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
                                malesuada lacus ex, sit amet blandit leo lobortis eget.
                            </AccordionDetails>
                        </Accordion>
                    </ListItem>
                    <ListItem>
                        <Accordion>
                            <AccordionSummary
                                expandIcon={<DownOutlined />}
                                aria-controls="panel1-content"
                                id="panel1-header"
                            >
                                <Typography component="span">Step 1</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
                                malesuada lacus ex, sit amet blandit leo lobortis eget.
                            </AccordionDetails>
                        </Accordion>
                    </ListItem>
                </List>
            </Box>
        </Box>
    );
}

export default ResultComponent;