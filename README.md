# AccessaNote
App that helps students record professor during class and take notes automatically.

### OpenAI API Key Setup
To make API calls to OpenAI, we need an API key that cannot be pushed to the repo. To set up an API key:
* Register for an account and get a key: https://help.openai.com/en/articles/4936850-where-do-i-find-my-secret-api-key
* Add to ```local.properties``` the following line : ```OPENAI_API_KEY=<YOUR_KEY>``` (no spaces)

The same key is currently used for both note transcription and summarization

### References
Google SignIn: https://betterprogramming.pub/jetpack-compose-theming-shapes-3f3cc8df7e5c
