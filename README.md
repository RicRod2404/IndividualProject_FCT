### Project Name

ADC Projeto, Edição 2023/24

### Description

This project is a simple website built using Java with Jersey for RESTful web services, and it's deployed on Google
Cloud App Engine. The purpose of this website is to manage user information, including registration, login/logout, and
standard profile and personal information management.

### Installation

To run this project locally, follow these steps:

1. Clone the repository with `git clone https://github.com/RicRod2404/APDC-2024-Individual` (*or another
   GitHub compatible method*).
2. Install Maven dependencies with `mvn clean install`.
3. Set up Google Cloud SDK and authenticate with your account.
4. Set up a local Google Cloud App Engine environment: `gcloud components install`
   and `gcloud init`.
5. Set up the following environment variables:
    - `DATASTORE_USE_PROJECT_ID_AS_APP_ID=true`: to use the project ID as the App Engine ID.
    - `DATASTORE_DATASET=apdc-2024-individual-418722`: the dataset ID.
    - `DATASTORE_PROJECT_ID=apdc-2024-individual-418722`: the project ID.
6. Run the application locally: `mvn clean package appengine:run`
7. Access the website at `http://localhost:8080`.

To deploy this project to Google Cloud App Engine, follow these steps:

1. Set up a Google Cloud Platform project and enable App Engine.
2. Configure `web.xml` and `appengine-web.xml` with your project ID and other configurations.
3. Deploy the application: `mvn clean package appengine:deploy`.
4. Access the deployed website at `https://apdc-2024-individual-418722.oa.r.appspot.com`.

### Usage

Once the application is running, you should see the home page. From there you can access the following pages:

- `/create-user`: to register a new user.
- `/show-token`: to show the current user's token.
- `/login`: to log in with an existing user.

If logged in, additional features are available (*please note that different roles have different access rights*):

- `/change-role`: to change a user's role.
- `/change-status`: to change a user's status.
- `/change-password`: to change the current user's password.
- `/list-users`: to list registered users.
- `/update-user`: to update a user's information.
- `/delete-user`: to delete a user.

### Contributing

Contributions are welcome! If you'd like to contribute to this project, please get in touch with me here on GitHub.

### License

This project is licensed under the MIT License as stated below.

Copyright (c) 2024 Ricardo Pereira Rodrigues

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.