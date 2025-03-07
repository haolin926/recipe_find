import React from 'react';
import {Avatar, List, Spin} from 'antd';
import "./ResultListComponent.css";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import {useNavigate} from "react-router-dom";
import axios from "axios";


const ResultListComponent = ({searchResult}) => {
    const data = searchResult;

    const navigate = useNavigate();
    const handleTitleClick = async (item) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/recipe/id?queryId=${item.recipeApiId}`);
            if(response.status === 200) {
                const recipe = response.data;
                navigate('/result', {state: {recipe}});
            } else {
                console.log("error");
            }
        } catch (error) {
            console.log(error);
        }
    };
    return (
        <Box sx={{ width: '100%', height: "100%" }}>
            <AppBar className={"commonHeader"} position={"static"}>
                <h2>Search Result</h2>
            </AppBar>
            <Box className={"listContainer"}>
                <List
                    itemLayout="horizontal"
                    dataSource={data}
                    pagination={{
                        pageSize: 5,
                    }}
                    style={{minHeight:"100%"}}
                    renderItem={(item) => (
                        <List.Item>
                            <List.Item.Meta
                                avatar={
                                <div className={"imageContainer"}>
                                    <Avatar className = "searchListImage" src={item.image}  alt={"searchresult"}/>
                                </div>
                                }
                                title={<a onClick={() => handleTitleClick(item)}>{item.name}</a>}
                                description="Ant Design, a design language for background applications, is refined by Ant UED Team"
                            />
                        </List.Item>
                    )}
                />
            </Box>
        </Box>
    );
}

export default ResultListComponent;