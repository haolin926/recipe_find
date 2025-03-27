import * as React from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import {Drawer, Menu} from "antd";
import "./sideBar.css"
import PropTypes from "prop-types";

function ResponsiveDrawer({visible, toggleDrawer}) {
    const navigate = useNavigate();
    const location = useLocation();

    const handleMenuClick = (key) => {
        navigate(key);
        toggleDrawer(); // Close the drawer after navigation
    };

    return (
        <Drawer title="Menu" placement="left" onClose={toggleDrawer} open={visible}
                variant="temporary"
                width={300}>
            <Menu
                mode="inline"
                onClick={({ key }) => handleMenuClick(key)}
                selectedKeys={[location.pathname]}
                items={[
                    { key: "/", label: "Home" },
                    { key: "/mealplan", label: "Meal Plan" },
                    { key: "/savedrecipes", label: "Saved Recipes" }
                ]}
            />
        </Drawer>
    );
}

ResponsiveDrawer.propTypes = {
    visible: PropTypes.bool.isRequired,
    toggleDrawer: PropTypes.func.isRequired
}

export default ResponsiveDrawer;