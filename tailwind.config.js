const {fontFamily} = require("tailwindcss/defaultTheme");

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "src/main/kotlin/fairu/frontend/**/*.kt",
        "src/main/resources/assets/**/*.{html,css}"
    ],
    theme: {
        extend: {
            fontFamily: {
                sans: ["Inter", ...fontFamily.sans]
            }
        },
    },
    plugins: [],
}

