import React from 'react';
import {Avatar, Button, List, Popover, Tag, Typography} from 'antd';
import "./ResultListComponent.css";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import {useNavigate} from "react-router-dom";
import PropTypes from "prop-types";
import {Link} from "@mui/material";


const ResultListComponent = ({ searchResult }) => {
    const navigate = useNavigate();

    const handleTitleClick = (item) => {
        navigate('/result', { state: { recipeId: item.recipeApiId } });
    };

    return (
        <Box sx={{ width: '100%', height: "100%" }}>
            <AppBar className={"commonHeader"} position={"static"}>
                <h2>Search Result</h2>
            </AppBar>

            {/* List Container */}
            <Box className="listContainer">
                {searchResult && searchResult.length > 0 ? (
                    <List
                        itemLayout="horizontal"
                        dataSource={searchResult}
                        pagination={{ pageSize: 5 }}
                        style={{ minHeight: "100%" }}
                        renderItem={(item) => {
                            const hasUsedIngredients = item.usedIngredients && item.usedIngredients.length > 0;

                            return (
                                <List.Item>
                                    <List.Item.Meta
                                        avatar={
                                            <div className="imageContainer">
                                                <Avatar className="searchListImage" src={item.image} alt="Recipe Image" />
                                            </div>
                                        }
                                        title={
                                            <Link className={"ResultRecipeTitle"} onClick={() => handleTitleClick(item)}>{item.name}</Link>
                                        }
                                        description={
                                            <div id="descriptionContainer">

                                                <Typography className="description">
                                                    {item.description || "No description available"}
                                                </Typography>

                                                <div className="tagsContainer">
                                                    <div style={{ marginTop: "8px" }}>
                                                        {item.glutenFree && (
                                                            <Tag color="green" className="resultTags">
                                                                Gluten Free
                                                            </Tag>
                                                        )}
                                                        {item.dairyFree && (
                                                            <Tag color="blue" className="resultTags">
                                                                Dairy Free
                                                            </Tag>
                                                        )}
                                                        {item.vegetarian && (
                                                            <Tag color="green" className="resultTags">
                                                                Vegetarian
                                                            </Tag>
                                                        )}
                                                    </div>

                                                    <div id="ingredientInfoDiv">
                                                        {hasUsedIngredients && (
                                                            <Popover
                                                                content={<ul>{item.usedIngredients.map((ing, index) => <li key={`${ing}-${index}`}>{ing}</li>)}</ul>}
                                                                title="Ingredients Used"
                                                            >
                                                                <Button variant="contained">Ingredients Used</Button>
                                                            </Popover>
                                                        )}
                                                    </div>
                                                </div>
                                            </div>
                                        }
                                    />
                                </List.Item>
                            );
                        }}
                    />
                ) : (
                    <Box sx={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100%" }}>
                        <Typography variant="h6">No results found</Typography>
                    </Box>
                )}
            </Box>
        </Box>
    );
};

ResultListComponent.propTypes = {
    searchResult: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.number,
            name: PropTypes.string.isRequired,
            image: PropTypes.string,
            description: PropTypes.string,
        })
    ).isRequired,
};
export default ResultListComponent;