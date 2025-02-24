import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import List from "@mui/material/List";
import {Accordion, AccordionDetails, AccordionSummary, ListItem} from "@mui/material";
import {DownOutlined} from "@ant-design/icons";
import Typography from "@mui/material/Typography";
import React from "react";
import "./ResultComponent.css";
const ResultComponent = ({instructions}) => {

    return (
        <Box className="bottomContainerPaper">
            <AppBar position="static" className={"commonHeader"}>
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
                    {instructions.map((instruction, index) => (
                        <ListItem key={index}>
                            <Accordion sx={{ width: "100%" }}>
                                <AccordionSummary
                                    expandIcon={<DownOutlined />}
                                    aria-controls={`panel${index}-content`}
                                    id={`panel${index}-header`}
                                >
                                    <Typography component="span">Step {index + 1}</Typography>
                                </AccordionSummary>
                                <AccordionDetails>
                                    {instruction}
                                </AccordionDetails>
                            </Accordion>
                        </ListItem>
                    ))}
                </List>
            </Box>
        </Box>
    );
}

export default ResultComponent;