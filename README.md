# College Enrollment Gender Gap Simulator

This is a JavaFX application that simulates how different factors
(HS performance, wage premium, job availability, etc.) affect the
college enrollment gender gap over time.

## How to Run (Fastest Way – IntelliJ)

1. Install:
   - Java 17 or later
   - IntelliJ IDEA (Community Edition is fine)
   - JavaFX SDK (https://gluonhq.com/products/javafx/)

2. Clone this repository:
   Paste this link where it says URL: https://github.com/SDeDatta/RealIPCapModel

3. Open the project in IntelliJ:
   - `File → Open…` → select the project folder

4. Add JavaFX to the project:
   - `File → Project Structure → Libraries → + → Java →` select the `javafx-sdk/lib` folder

5. Run the app:
   - Open `CollegeEnrollmentSimulator.java`
   - Click the green ▶ next to `main` or right-click → `Run 'CollegeEnrollmentSimulator.main()'`

## What the Simulator Shows

The simulator lets you:
- Adjust weights on factors like HS performance, wage premium, job growth, and culture
- See how those factors affect modeled male vs. female college enrollment over time
- Compare the model to historical data (1980–2024)
