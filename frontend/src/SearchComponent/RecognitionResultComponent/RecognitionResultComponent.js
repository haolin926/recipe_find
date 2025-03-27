import Box from "@mui/material/Box";
import "./RecognitionResultComponent.css";
import {Link, Paper} from "@mui/material";
import {Button, Tag, Typography, Flex, Progress} from 'antd'
import React from 'react';
import AppBar from "@mui/material/AppBar";
import PropTypes from "prop-types";

function RecognitionResultComponent ( {predictions, image, onSearchByName, onSearchByIngredient}) {
    const [selectedTags, setSelectedTags] = React.useState([]);
    console.log(predictions);
    const handleChange = (tag, checked) => {
        const nextSelectedTags = checked
            ? [...selectedTags, tag]
            : selectedTags.filter((t) => t !== tag);
        setSelectedTags(nextSelectedTags);
    };

    const handleSearchByName = (name) => {
        onSearchByName(name);
    }

    const handleSearchByIngredient = (ingredients) => {
        onSearchByIngredient(ingredients);
    }

    return (
          <Box className={"container"}>
              <Box className={"predictImageContainer"}>
                  <img className="recognizeImage" src={URL.createObjectURL(image)} alt={"recognize"}/>
              </Box>
              <Box className={"predictionResultContainer"}>
                  <Paper elevation={3} className={"predictResult"}>
                    <AppBar position="static" className={"commonHeader"}>
                        <h3>Prediction Result</h3>
                    </AppBar>
                    <Flex className={"nameAndProbability"} vertical>
                        {predictions && predictions.predictName.length > 0 ? (
                            predictions.predictName.map((prediction, index) => (
                                <div className={"predict_probability_container"} key={`prediction-${prediction.name}-${index}`}>
                                    <Link underline="none" className="predictionText" onClick={() => handleSearchByName(prediction.name)}>{prediction.name}</Link>
                                    <Progress
                                        percent={(prediction.probability * 100).toFixed(2)}
                                        strokeColor={"#e67e22"}
                                        style={{ width: "100%" }}
                                        showInfo={true}
                                        percentPosition={{ align: 'end' }}
                                        size={{height:15}}
                                    />
                                </div>
                            ))
                        ) : (
                            <Box className={"errorMessageContainer"}>
                                <Typography.Text>No predictions available</Typography.Text>
                            </Box>
                        )}
                    </Flex>
                  </Paper>
                  <Paper elevation={3} className={"predictResult"}>
                      <AppBar position="static" className={"commonHeader"}>
                          <h3>Ingredient Found</h3>
                      </AppBar>
                      <Flex gap={4} wrap className={"nameAndProbability"}>
                          {predictions && predictions.detectedIngredients.length > 0 ? (
                              predictions.detectedIngredients.map((ingredient) => (
                                  <Tag.CheckableTag
                                      className={"ingredientTags"}
                                      key={ingredient}
                                      checked={selectedTags.includes(ingredient)}
                                      onChange={(checked) => handleChange(ingredient, checked)}
                                  >
                                      {ingredient}
                                  </Tag.CheckableTag>
                              ))
                              ) : (
                              <Box className={"errorMessageContainer"}>
                                  <Typography.Text>No ingredients found</Typography.Text>
                              </Box>
                            )}
                      </Flex>
                      <Button className="searchByIngredientButton" onClick={() => handleSearchByIngredient(selectedTags)}>
                          Search By Selected Ingredients
                      </Button>
                  </Paper>
              </Box>
        </Box>
    );
}

RecognitionResultComponent.propTypes = {
    predictions: PropTypes.shape({
        predictName: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string.isRequired,
                probability: PropTypes.number.isRequired
            })
        ).isRequired,
        detectedIngredients: PropTypes.arrayOf(PropTypes.string).isRequired
    }).isRequired,
    image: PropTypes.instanceOf(File).isRequired,
    onSearchByName: PropTypes.func.isRequired,
    onSearchByIngredient: PropTypes.func.isRequired
};

export default RecognitionResultComponent;