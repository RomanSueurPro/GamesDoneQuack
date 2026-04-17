export const environment = {
    production: false,
    apiBaseUrl: 'http://localhost:8080',

};

//In the future, we will need also a environment.prod.ts
//to use .prod.ts, we will then need to go to angular.json and create *somewhere*
//a "fileReplacements" key with the .prod.ts path.
//This will ensure when lauched with production param tha app will load the
//correct URL base