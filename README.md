## ClickCart - Your Ultimate E-Commerce Shopping App
### Screenshots
![image](https://github.com/KarimovEldar/ClickCart/assets/142349187/01b968b7-cef0-4de5-9392-0546edee793b)
![image](https://github.com/KarimovEldar/ClickCart/assets/142349187/95ccf01a-4014-4fa4-aab5-93bdd39999c8)
![image](https://github.com/KarimovEldar/ClickCart/assets/142349187/e5f28dcd-51c9-4771-8226-40a95968b7cb)
![image](https://github.com/KarimovEldar/ClickCart/assets/142349187/db37d07a-c6db-445a-8f64-c93ba4397fb0)
![image](https://github.com/KarimovEldar/ClickCart/assets/142349187/9620c5fd-5167-4857-8858-89eda7284d3e)

### Figma Design Link
You can explore the Figma design for this project [here](https://www.figma.com/file/ijaQwVIHjUr6wCnvEG63CO/Untitled?type=design&node-id=0-1&mode=design&t=QWapesuzcXBi7Cbc-0).

### Libraries and technologies used

- **Navigation Component**: I leverage the Navigation Component to streamline navigation within the app. One activity hosts multiple fragments, making the app structure efficient and easy to manage.

- **MVVM & LiveData**: My architecture follows the Model-View-ViewModel pattern, keeping logic and views separate. LiveData is used to handle data changes and maintain state even when screen configurations change.

- **Firebase Authentication**: I use Firebase Auth to manage user accounts, handle login and registration, and ensure a secure authentication process.

- **Firebase Firestore**: For cloud-based data storage, I rely on Firebase Firestore, a real-time NoSQL database. This enables efficient data management for the system.

- **Firebase Storage**: Images, including product images and user profile pictures, are stored securely in Firebase Storage.

- **Cloud Firestore**: In addition to Firebase Firestore, I utilize Cloud Firestore to enhance data synchronization and provide real-time updates, ensuring your app always displays the latest information.

- **Room Database**: Locally, I employ Room Database to store and manage app data, providing an efficient and reliable way to work with local databases.

- **Hilt (Dependency Injection)**: I use Hilt for Dependency Injection, ensuring that my app's components are injected with the required dependencies, making the code clean and maintainable.

- **Coroutines**: I employ Coroutines to execute background tasks efficiently, ensuring smooth performance and responsiveness.

- **View Binding**: To simplify the view setup process, I use View Binding, eliminating the need to manually inflate views.

- **Coil**: Image loading is made effortless with Coil. It caches and loads images seamlessly into ImageViews for an optimal user experience.
