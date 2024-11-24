import React from "react";
import {useLocation} from "react-router-dom";
import "./ResultComponent.css";
const ResultComponent = () => {
    const location = useLocation();
    const data = location.state?.data;
    if (!data) {
        return  <div>No data</div>;
    }

    const {name, image, instructions, ingredients, nutrition} = data;
    return (
        <div>
            <h2 id={"header"}>Result Page</h2>
            <div id={"content"}>
                <div id={"upper_content"}>
                    <div id={"dish"}>
                        <div id={"recipe_name"}>
                            {name}
                        </div>
                        <div>
                            <img src={image} alt={"Recipe"} id={"recipe_image"}/>
                        </div>
                    </div>
                    <div id={"instruction_list"}>
                        <h2>Instructions</h2>
                        <ol id={"steps"}>
                            {instructions.map((instruction, index) =>
                                <li key={index}>{instruction}</li>)}
                        </ol>
                    </div>
                </div>
                <div id={"lower_content"}>
                    <div id={"ingredient_list"} className={"info_list"}>
                        <h2>Ingredients</h2>
                        <ul>
                            {ingredients.map((ingredient, index) => (
                                <li key={index}>{ingredient.ingredient}: {ingredient.amount_unit}</li>
                            ))}
                        </ul>
                    </div>

                    <div id={"nutrition_list"} className={"info_list"}>
                        <h2>Nutrition</h2>
                        <ul>
                            {nutrition.map((nutrient, index) => (
                                <li key={index}>{nutrient.nutrient}: {nutrient.amount_unit}</li>
                            ))}
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ResultComponent;