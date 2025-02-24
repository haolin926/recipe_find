import Box from "@mui/material/Box";
import "./RecognitionResultComponent.css";
import {Link, Paper} from "@mui/material";
import {Button, Tag} from 'antd'
import React from 'react';
import {Divider, Flex, Progress} from "antd";
import {Typography} from 'antd';

function RecognitionResultComponent ( {predictions, image, onSearchByName}) {
    const [selectedTags, setSelectedTags] = React.useState([]);
    const handleChange = (tag, checked) => {
        const nextSelectedTags = checked
            ? [...selectedTags, tag]
            : selectedTags.filter((t) => t !== tag);
        setSelectedTags(nextSelectedTags);
    };

    const handleSearchByName = (name) => {
        onSearchByName(name);
    }

    return (
      <Box className={"container"}>
          <Box className={"predictImageContainer"}>
              <img className="recognizeImage" src={URL.createObjectURL(image)} alt={"recognize"}/>
          </Box>
          <Paper className={"predictionResultContainer"}>
              <Box className={"predictResult"}>
                <Typography.Title level={2}>Prediction Result</Typography.Title>
                <Flex className={"nameAndProbability"} vertical>
                    {predictions && predictions.predictName.length > 0 ? (
                        predictions.predictName.map((prediction, index) => (
                            <div className={"predict_probability_container"} key={index}>
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
                        <Typography.Text>No predictions available</Typography.Text>
                    )}
                </Flex>
              </Box>
              <Divider></Divider>
              <Box className={"predictResult"}>
                  <Typography.Title level={2}>Ingredients Found</Typography.Title>
                  <Flex gap={4} wrap>
                      {predictions.detectedIngredients.map((ingredient) => (
                          <Tag.CheckableTag
                              className={"ingredientTags"}
                              key={ingredient}
                              checked={selectedTags.includes(ingredient)}
                              onChange={(checked) => handleChange(ingredient, checked)}
                          >
                              {ingredient}
                          </Tag.CheckableTag>
                      ))}
                  </Flex>
                  <Button className="bottomRightButton">Search By Selected Ingredients</Button>
              </Box>
          </Paper>
      </Box>
    );
}

export default RecognitionResultComponent;