# Frugl

## Software Design Project - Fall 2025
### MONEY TREES (Group 3)

## Project Summary

Frugl is a swing-based Personal Financial Management application that transforms raw bank statements into actionable insight. It simplifies financial tracking by allowing users to import bank statements for automated categorization and autosaving. Also, the app promotes mindful spending through intuitive visualizations and a gamified system for setting and tracking monthly spending goals.

The user navigates our app from the main dashboard, where they can import bank statements from their local files and select a date range to visualize a breakdown of spending, inflows, and outflows. The user can also view monthly individual transactions and set goals using an interactive “financial forest,” where each goal creates a tree in their goal forest.

### Structure of the app
Our application is built on Clean Architecture and SOLID principles, with excellent Code Quality. Thus, our front-end UI elements operate independently of our core back-end logic, improving maintainability and collaboration. Our app runs locally, processes bank statements from JSON files using Gemini, and displays colourful charts via Charts.io.
