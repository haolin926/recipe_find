import React, {Component} from "react";
import {withRouter} from "../withRouter/withRouter";
import { Button } from 'antd';
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import "./HomeComponent.css";
import PropTypes from "prop-types";
class HomeComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            image: null,
            previewURL: null,
            name: null,
            loading: false,
            dialogOpen: false,
            predictName: null,
        };
    }

    handleSearchByNameClick = () => {
        this.props.navigate("/search", {state: {value:"1"}});
    }

    handleSearchByImageClick = () => {
        this.props.navigate("/search", {state: {value:"2"}});
    }
    handleSearchByIngredientClick = () => {
        this.props.navigate("/search", {state: {value:"3"}});
    }

    render() {
        return (
            <Box className={"bodyBackground"}>
                <Box id={"bodyContainer"}>
                        <Box id={"contentContainer"}>
                            <Box sx={{width:"30%"}}>
                                <Typography variant="h3" sx={{color:"#141414"}}>LOOK FOR RECIPES</Typography>
                            </Box>
                            <Box id={"buttonContainer"}>
                                <Button className="mainButton" type="text" size="large" onClick={this.handleSearchByNameClick}>Search Recipe by Name</Button>
                                <Button className="mainButton" type="text" size="large" onClick={this.handleSearchByImageClick}>Search by Uploading Image</Button>
                                <Button className="mainButton" type="text" size="large" onClick={this.handleSearchByIngredientClick}>Search by Ingredients</Button>
                            </Box>
                        </Box>
                </Box>
            </Box>
        );
    }
}

HomeComponent.propTypes = {
    navigate: PropTypes.func.isRequired,
};

export default withRouter(HomeComponent);