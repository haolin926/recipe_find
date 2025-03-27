import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import React, {useState} from "react";
import "./ResultComponent.css";
import PropTypes from 'prop-types';
import { Steps } from "antd";

const ResultComponent = ({instructions}) => {
    const { Step } = Steps;
    const [current, setCurrent] = useState(0);

    const onChange = (value) => {
        setCurrent(value);
    };

    return (
        <Box className="bottomContainerPaper">
            <AppBar position="static" className={"commonHeader"}>
                <h1>Instructions</h1>
            </AppBar>
            <Box className="resultInfoContainer">
                <Steps
                    direction="vertical"
                    current={current}
                    onChange={onChange}
                    className="instructionList"
                >
                    {instructions.map((instruction, index) => (
                        <Step
                            key={index}
                            title={`Step ${index + 1}`}
                            description={<Typography>{instruction}</Typography>}
                        />
                    ))}
                </Steps>
            </Box>
        </Box>
    );
};

ResultComponent.propTypes = {
    instructions: PropTypes.arrayOf(PropTypes.string).isRequired,
};
export default ResultComponent;