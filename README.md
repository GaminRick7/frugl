# frugl

## Software Design Project - Fall 2025
### MONEY TREES (Group 3)

## Project Summary

Frugl is a swing-based Personal Financial Management application that transforms raw bank statements into actionable insight. It simplifies financial tracking by allowing users to import bank statements for automated categorization and autosaving. Also, the app promotes mindful spending through intuitive visualizations and a gamified system for setting and tracking monthly spending goals.

The user navigates our app from the main dashboard, where they can import bank statements from their local files and select a date range to visualize a breakdown of spending, inflows, and outflows. The user can also view monthly individual transactions and set goals using an interactive “financial forest,” where each goal creates a tree in their goal forest.

### Structure of the app
Our application is built on Clean Architecture and SOLID principles, with excellent Code Quality. Thus, our front-end UI elements operate independently of our core back-end logic, improving maintainability and collaboration. Our app runs locally, processes bank statements from JSON files using Gemini, and displays colourful charts via QuickChart.io.

## User stories

| #  | User Story  | Description | Developer |
| :- | :-----------| :--------- | :-----: |
| 1  | Import Bank Statements | As a user, I want to import my bank statements in JSON format so that the app can automatically categorize my transactions. | Parsa Nabifar |
| 2  | View Transaction | As a user, I want to view my list of organized transactions by month to review my particular monthly transactions. | Yunji Hwang |
| 3  | Load Dashboard (Pie Chart) | As a user, I want to view a visual breakdown of my spending by category for a selected month so that I can understand my spending habits. | Kosar Hemmati |
| 4 | Load Dashboard (Time Chart) | As a user, I want to view a monthly summary of my income and expenses over time so that I can identify my financial trends. | Kosar Hemmati |
| 5 | Goal Trees| As a user, I want to set monthly spending goals by category and track my progress through interactive visualizations so that I can manage my budget more effectively and stay motivated to save. | Kerem Berk Bozkurt |
| 6 | Autosaving | As a user, I want my transaction and goal data to be automatically saved periodically in the background so that I don't lose my work if the application crashes or I forget to manually save. | Raihaan Sandhu |


## API and Data Used

API link:
API Usage: Categorizing bank statements into categories
 Income, Rent & Utilities, Transportation, Food & Dining, Shopping, Entertainment, Other
Instructions:
Instructions (Setting API key in env) 
 GEMINI_API_KEY=your_api_token

### Charts.io API
API Link: https://quickchart.io/chart?c=_{your chart here}_
API Usage: Visualizing financial data by spending category (pie chart) and by inflow and outlow over time (time chart)
Instructions: _KOSAR TO ADD_

### JSON data
Transactions.json
Source_Categories.json
Goals.json

## Screenshots

<img src="screenshots/import.png" alt="import.png image" style="width:50%;">

<img src="screenshots/transaction.png" alt="transaction.png image" style="width:50%;">

<img src="screenshots/dashboard.png" alt="dashboard.png image" style="width:50%;">

<img src="screenshots/tree.png" alt="tree.png image" style="width:50%;">

<img src="screenshots/autosave.png" alt="autosave.png image" style="width:25%;">

<img src="screenshots/checkStyle.png" alt="checkStyle.png image" style="width:50%;">
