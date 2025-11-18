package simulator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.*;

    /**
     * COMPLETE COLLEGE ENROLLMENT GENDER GAP SIMULATOR
     *
     * INSTRUCTIONS FOR INTELLIJ:
     * 1. Create a new JavaFX project in IntelliJ
     * 2. Create a file called "CollegeEnrollmentSimulator.java"
     * 3. Copy and paste ALL of this code into that file
     * 4. Make sure you have JavaFX set up (File > Project Structure > Libraries > Add JavaFX)
     * 5. Right-click on the file and select "Run 'CollegeEnrollmentSimulator.main()'"
     *
     * If you need help setting up JavaFX in IntelliJ:
     * - Download JavaFX SDK from: https://gluonhq.com/products/javafx/
     * - Add VM options: --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
     */

    public class CollegeEnrollmentSimulator extends Application {

        // Simulator instance
        private Simulator simulator;

        // UI Components
        private Map<String, Slider> weightSliders = new HashMap<>();
        private Map<String, Slider> factorSliders = new HashMap<>();
        private Label femaleResultLabel;
        private Label maleResultLabel;
        private Label gapResultLabel;
        private LineChart<Number, Number> projectionChart;
        private LineChart<Number, Number> historicalChart;
        private Slider projectionYearsSlider;

        @Override
        public void start(Stage primaryStage) {
            simulator = new Simulator();

            // Main layout
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #f0f4f8;");

            // Header
            VBox header = new VBox(10);
            Label title = new Label("College Enrollment Gender Gap Simulator");
            title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
            Label subtitle = new Label("Adjust factors to simulate their impact on college enrollment by gender (1980-2024+)");
            subtitle.setFont(Font.font("Arial", 13));
            header.getChildren().addAll(title, subtitle);
            root.setTop(header);

            // Scrollable center content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            VBox content = new VBox(20);
            content.setPadding(new Insets(20));

            // Results display
            content.getChildren().add(createResultsDisplay());

            // Factor weights
            content.getChildren().add(createWeightsSection());

            // Factor adjustments
            content.getChildren().add(createFactorsSection());

            // Projection settings
            content.getChildren().add(createProjectionSettings());

            // Charts
            content.getChildren().add(createProjectionChart());
            content.getChildren().add(createHistoricalChart());

            scrollPane.setContent(content);
            root.setCenter(scrollPane);

            // Initial simulation
            updateSimulation();

            Scene scene = new Scene(root, 1200, 900);
            primaryStage.setTitle("College Enrollment Gender Gap Simulator");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        private HBox createResultsDisplay() {
            HBox box = new HBox(20);
            box.setAlignment(Pos.CENTER);

            VBox femaleBox = createResultCard("Female Enrollment", "#ec4899");
            femaleResultLabel = new Label("59.4%");
            femaleResultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            femaleResultLabel.setStyle("-fx-text-fill: #ec4899;");
            femaleBox.getChildren().add(femaleResultLabel);

            VBox maleBox = createResultCard("Male Enrollment", "#3b82f6");
            maleResultLabel = new Label("40.6%");
            maleResultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            maleResultLabel.setStyle("-fx-text-fill: #3b82f6;");
            maleBox.getChildren().add(maleResultLabel);

            VBox gapBox = createResultCard("Gender Gap (F-M)", "#a855f7");
            gapResultLabel = new Label("+18.8 pts");
            gapResultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            gapResultLabel.setStyle("-fx-text-fill: #a855f7;");
            gapBox.getChildren().add(gapResultLabel);

            box.getChildren().addAll(femaleBox, maleBox, gapBox);
            return box;
        }

        private VBox createResultCard(String titleText, String color) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(20));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
            card.setAlignment(Pos.CENTER);
            card.setPrefWidth(250);

            Label title = new Label(titleText);
            title.setFont(Font.font("Arial", 11));
            title.setStyle("-fx-text-fill: #64748b;");
            card.getChildren().add(title);
            return card;
        }

        private VBox createWeightsSection() {
            VBox section = createSection("Factor Weights (Importance)");
            GridPane grid = new GridPane();
            grid.setHgap(15);
            grid.setVgap(12);
            grid.setPadding(new Insets(10));

            String[][] weights = {
                    {"hsPerformance", "HS Performance", "0.25"},
                    {"wagePremium", "Wage Premium", "0.15"},  // Changed from 0.30
                    {"fieldJobAvailability", "Field/Job Availability", "0.20"},
                    {"opportunityCost", "Opportunity Cost", "0.15"},
                    {"culturalPolitical", "Cultural/Political", "0.25"}  // Changed from 0.10
            };

            for (int i = 0; i < weights.length; i++) {
                String key = weights[i][0];
                Label label = new Label(weights[i][1] + ":");
                label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));

                Slider slider = new Slider(0, 1, Double.parseDouble(weights[i][2]));
                slider.setPrefWidth(200);
                slider.valueProperty().addListener((obs, old, val) -> updateSimulation());
                weightSliders.put(key, slider);

                Label valLabel = new Label(String.format("%.0f%%", slider.getValue() * 100));
                slider.valueProperty().addListener((obs, old, val) ->
                        valLabel.setText(String.format("%.0f%%", val.doubleValue() * 100)));

                int row = i / 2;
                int col = (i % 2) * 3;
                grid.add(label, col, row);
                grid.add(slider, col + 1, row);
                grid.add(valLabel, col + 2, row);
            }

            section.getChildren().add(grid);
            return section;
        }

        private VBox createFactorsSection() {
            VBox section = createSection("Factor Adjustments (Multipliers)");
            GridPane grid = new GridPane();
            grid.setHgap(15);
            grid.setVgap(12);
            grid.setPadding(new Insets(10));

            String[][] factors = {
                    {"hsGradGap", "HS Graduation Gap"},
                    {"wagePremiumFemale", "Female Wage Premium"},
                    {"wagePremiumMale", "Male Wage Premium"},
                    {"healthcareGrowth", "Healthcare Growth"},
                    {"stemGrowth", "STEM Growth"},
                    {"constructionJobs", "Construction Jobs"},
                    {"manufacturingJobs", "Manufacturing Jobs"},
                    {"culturalShift", "Cultural Shift"}
            };

            for (int i = 0; i < factors.length; i++) {
                String key = factors[i][0];
                Label label = new Label(factors[i][1] + ":");
                label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));

                Slider slider = new Slider(0, 2, 1.0);
                slider.setPrefWidth(200);
                slider.valueProperty().addListener((obs, old, val) -> updateSimulation());
                factorSliders.put(key, slider);

                Label valLabel = new Label("100%");
                slider.valueProperty().addListener((obs, old, val) ->
                        valLabel.setText(String.format("%.0f%%", val.doubleValue() * 100)));

                int row = i / 2;
                int col = (i % 2) * 3;
                grid.add(label, col, row);
                grid.add(slider, col + 1, row);
                grid.add(valLabel, col + 2, row);
            }

            section.getChildren().add(grid);
            return section;
        }

        private VBox createProjectionSettings() {
            VBox section = createSection("Projection Settings");
            HBox controls = new HBox(20);
            controls.setAlignment(Pos.CENTER_LEFT);
            controls.setPadding(new Insets(10));

            Label label = new Label("Years to Project:");
            projectionYearsSlider = new Slider(5, 30, 10);
            projectionYearsSlider.setShowTickLabels(true);
            projectionYearsSlider.setMajorTickUnit(5);
            projectionYearsSlider.setSnapToTicks(true);
            projectionYearsSlider.setPrefWidth(300);
            projectionYearsSlider.valueProperty().addListener((obs, old, val) -> updateSimulation());

            Button resetBtn = new Button("Reset to Defaults");
            resetBtn.setOnAction(e -> resetToDefaults());
            resetBtn.setStyle("-fx-background-color: #e2e8f0; -fx-font-weight: bold;");

            controls.getChildren().addAll(label, projectionYearsSlider, resetBtn);
            section.getChildren().add(controls);
            return section;
        }

        private VBox createProjectionChart() {
            VBox section = createSection("Enrollment Projection");

            NumberAxis xAxis = new NumberAxis(1980,2055,5);
            xAxis.setLabel("Year");
            xAxis.setAutoRanging(false);
            NumberAxis yAxis = new NumberAxis(0, 100, 10);
            yAxis.setLabel("Enrollment %");

            projectionChart = new LineChart<>(xAxis, yAxis);
            projectionChart.setTitle("Future Projection Based on Current Settings");
            projectionChart.setPrefHeight(300);
            projectionChart.setCreateSymbols(false);

            section.getChildren().add(projectionChart);
            return section;
        }

        private VBox createHistoricalChart() {
            VBox section = createSection("Historical Data (1980-2024)");

            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Year");
            NumberAxis yAxis = new NumberAxis(0, 100, 10);
            yAxis.setLabel("Enrollment %");

            historicalChart = new LineChart<>(xAxis, yAxis);
            historicalChart.setTitle("Actual Historical Enrollment");
            historicalChart.setPrefHeight(250);
            historicalChart.setCreateSymbols(true);

            // Populate with historical data
            XYChart.Series<Number, Number> femaleSeries = new XYChart.Series<>();
            femaleSeries.setName("Female %");
            XYChart.Series<Number, Number> maleSeries = new XYChart.Series<>();
            maleSeries.setName("Male %");

            double[][] historicalData = {
                    {1980, 50.3, 49.7},
                    {1990, 53.4, 46.6},
                    {2000, 57.7, 42.3},
                    {2010, 58.6, 41.4},
                    {2020, 58.5, 41.5},
                    {2024, 59.4, 40.6}
            };

            for (double[] data : historicalData) {
                femaleSeries.getData().add(new XYChart.Data<>(data[0], data[1]));
                maleSeries.getData().add(new XYChart.Data<>(data[0], data[2]));
            }

            historicalChart.getData().addAll(femaleSeries, maleSeries);
            section.getChildren().add(historicalChart);
            return section;
        }

        private VBox createSection(String titleText) {
            VBox section = new VBox(10);
            section.setPadding(new Insets(15));
            section.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");

            Label title = new Label(titleText);
            title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            section.getChildren().add(title);
            return section;
        }

        private void updateSimulation() {
            // Update model weights
            simulator.model.setFactorWeight("hsPerformance", weightSliders.get("hsPerformance").getValue());
            simulator.model.setFactorWeight("wagePremium", weightSliders.get("wagePremium").getValue());
            simulator.model.setFactorWeight("fieldJobAvailability", weightSliders.get("fieldJobAvailability").getValue());
            simulator.model.setFactorWeight("opportunityCost", weightSliders.get("opportunityCost").getValue());
            simulator.model.setFactorWeight("culturalPolitical", weightSliders.get("culturalPolitical").getValue());

            // Update factor multipliers
            simulator.model.setFactorMultiplier("hsPerformance", null, factorSliders.get("hsGradGap").getValue());
            simulator.model.setFactorMultiplier("wagePremium", "female", factorSliders.get("wagePremiumFemale").getValue());
            simulator.model.setFactorMultiplier("wagePremium", "male", factorSliders.get("wagePremiumMale").getValue());
            simulator.model.setFactorMultiplier("fieldJobAvailability", "healthcare", factorSliders.get("healthcareGrowth").getValue());
            simulator.model.setFactorMultiplier("fieldJobAvailability", "stem", factorSliders.get("stemGrowth").getValue());
            simulator.model.setFactorMultiplier("opportunityCost", "construction", factorSliders.get("constructionJobs").getValue());
            simulator.model.setFactorMultiplier("opportunityCost", "manufacturing", factorSliders.get("manufacturingJobs").getValue());
            simulator.model.setFactorMultiplier("culturalPolitical", null, factorSliders.get("culturalShift").getValue());

            // Run projection
            int years = (int) projectionYearsSlider.getValue();
            List<EnrollmentResult> results = simulator.runProjection(years);

            // Update result labels
            EnrollmentResult current = results.get(0);
            femaleResultLabel.setText(String.format("%.1f%%", current.femaleShare));
            maleResultLabel.setText(String.format("%.1f%%", current.maleShare));
            gapResultLabel.setText(String.format("%+.1f pts", current.gap));

            // Update projection chart
            projectionChart.getData().clear();
            XYChart.Series<Number, Number> femaleSeries = new XYChart.Series<>();
            femaleSeries.setName("Female %");
            XYChart.Series<Number, Number> maleSeries = new XYChart.Series<>();
            maleSeries.setName("Male %");

            for (EnrollmentResult result : results) {
                femaleSeries.getData().add(new XYChart.Data<>(result.year, result.femaleShare));
                maleSeries.getData().add(new XYChart.Data<>(result.year, result.maleShare));
            }

            projectionChart.getData().addAll(femaleSeries, maleSeries);
        }

        private void resetToDefaults() {
            weightSliders.get("hsPerformance").setValue(0.25);
            weightSliders.get("wagePremium").setValue(0.30);
            weightSliders.get("fieldJobAvailability").setValue(0.20);
            weightSliders.get("opportunityCost").setValue(0.15);
            weightSliders.get("culturalPolitical").setValue(0.10);

            for (Slider slider : factorSliders.values()) {
                slider.setValue(1.0);
            }

            projectionYearsSlider.setValue(10);
            updateSimulation();
        }

        public static void main(String[] args) {
            launch(args);
        }

        // ============================================
        // MODEL CLASSES (Inner classes)
        // ============================================

        static class Simulator {
            EnrollmentModel model = new EnrollmentModel();
            int currentYear = 2024;

            List<EnrollmentResult> runProjection(int yearsAhead) {
                List<EnrollmentResult> results = new ArrayList<>();
                for (int i = 0; i <= yearsAhead; i++) {
                    results.add(model.calculateEnrollment(currentYear + i));
                }
                return results;
            }
        }

        static class EnrollmentModel {
            HistoricalData historicalData = new HistoricalData();
            Map<String, Factor> factors = new HashMap<>();
            double baseFemale = 0.50;
            double baseMale = 0.50;

            EnrollmentModel() {
                factors.put("hsPerformance", new HSPerformanceFactor(0.25, 1.0));
                factors.put("wagePremium", new WagePremiumFactor(0.15, 1.0, 1.0));  // Reduced from 0.30 to 0.15
                factors.put("fieldJobAvailability", new FieldJobAvailabilityFactor(0.20, 1.0, 1.0));
                factors.put("opportunityCost", new OpportunityCostFactor(0.15, 1.0, 1.0));
                factors.put("culturalPolitical", new CulturalPoliticalFactor(0.25, 1.0));  // Increased from 0.10 to 0.25c
            }

            EnrollmentResult calculateEnrollment(int year) {
                double totalGap = 0;
                for (Factor f : factors.values()) {
                    totalGap += f.calculateEffect(historicalData, year);
                }
                double yearFactor = Math.min(1.0, (year - 1980) / 20.0); // Ramp up over 20 years
                totalGap = totalGap * 0.55 * yearFactor;
                double female = baseFemale + totalGap;
                double male = baseMale - totalGap;
                double total = female + male;

                female = (female / total) * 100;
                male = (male / total) * 100;

                return new EnrollmentResult(year,
                        Math.max(0, Math.min(100, female)),
                        Math.max(0, Math.min(100, male)));
            }

            void setFactorWeight(String name, double weight) {
                if (factors.containsKey(name)) {
                    factors.get(name).weight = weight;
                }
            }

            void setFactorMultiplier(String factorName, String type, double value) {
                Factor f = factors.get(factorName);
                if (f instanceof HSPerformanceFactor) {
                    ((HSPerformanceFactor)f).multiplier = value;
                } else if (f instanceof WagePremiumFactor) {
                    WagePremiumFactor wpf = (WagePremiumFactor)f;
                    if ("female".equals(type)) wpf.femaleMultiplier = value;
                    else if ("male".equals(type)) wpf.maleMultiplier = value;
                } else if (f instanceof FieldJobAvailabilityFactor) {
                    FieldJobAvailabilityFactor fjf = (FieldJobAvailabilityFactor)f;
                    if ("healthcare".equals(type)) fjf.healthcareMultiplier = value;
                    else if ("stem".equals(type)) fjf.stemMultiplier = value;
                } else if (f instanceof OpportunityCostFactor) {
                    OpportunityCostFactor ocf = (OpportunityCostFactor)f;
                    if ("construction".equals(type)) ocf.constructionMultiplier = value;
                    else if ("manufacturing".equals(type)) ocf.manufacturingMultiplier = value;
                } else if (f instanceof CulturalPoliticalFactor) {
                    ((CulturalPoliticalFactor)f).multiplier = value;
                }
            }
        }

        static class EnrollmentResult {
            int year;
            double femaleShare, maleShare, gap;
            EnrollmentResult(int y, double f, double m) {
                year = y; femaleShare = f; maleShare = m; gap = f - m;
            }
        }

        static class HistoricalData {
            double interpolate(String factor, String subType, int year) {
                double t = (year - 1980) / 44.0;
                Map<String, double[]> data = new HashMap<>();
                data.put("hsGraduation.female", new double[]{73, 92});
                data.put("hsGraduation.male", new double[]{69, 85});
                data.put("wagePremium.female", new double[]{30, 75});
                data.put("wagePremium.male", new double[]{45, 55});
                data.put("jobGrowth.healthcare", new double[]{10.2, 23.4});
                data.put("jobGrowth.stem", new double[]{7.0, 10.78});
                data.put("jobGrowth.manufacturing", new double[]{19.3, 13.0});
                data.put("jobGrowth.construction", new double[]{4.5, 8.1});
                data.put("political.femaleLiberalShare", new double[]{20, 44});

                String key = factor + "." + subType;
                double[] values = data.get(key);
                if (values == null) return 0;
                return values[0] + t * (values[1] - values[0]);
            }
        }

        static abstract class Factor {
            double weight, multiplier;
            Factor(double w, double m) { weight = w; multiplier = m; }
            abstract double calculateEffect(HistoricalData data, int year);
        }

        static class HSPerformanceFactor extends Factor {
            HSPerformanceFactor(double w, double m) { super(w, m); }
            double calculateEffect(HistoricalData data, int year) {
                double femaleHS = data.interpolate("hsGraduation", "female", year);
                double maleHS = data.interpolate("hsGraduation", "male", year);
                return ((femaleHS - maleHS) / 100.0) * multiplier * weight;
            }
        }

        static class WagePremiumFactor extends Factor {
            double femaleMultiplier, maleMultiplier;
            WagePremiumFactor(double w, double fm, double mm) {
                super(w, 1.0); femaleMultiplier = fm; maleMultiplier = mm;
            }
            double calculateEffect(HistoricalData data, int year) {
                double fw = data.interpolate("wagePremium", "female", year);
                double mw = data.interpolate("wagePremium", "male", year);
                return ((fw * femaleMultiplier - mw * maleMultiplier) / 100.0) * weight;
            }
        }

        static class FieldJobAvailabilityFactor extends Factor {
            double healthcareMultiplier, stemMultiplier;
            FieldJobAvailabilityFactor(double w, double hm, double sm) {
                super(w, 1.0); healthcareMultiplier = hm; stemMultiplier = sm;
            }
            double calculateEffect(HistoricalData data, int year) {
                double hc1980 = data.interpolate("jobGrowth", "healthcare", 1980);
                double hc2024 = data.interpolate("jobGrowth", "healthcare", 2024);
                double st1980 = data.interpolate("jobGrowth", "stem", 1980);
                double st2024 = data.interpolate("jobGrowth", "stem", 2024);

                double hcGrowth = ((hc2024 - hc1980) / hc1980) * healthcareMultiplier;
                double stGrowth = ((st2024 - st1980) / st1980) * stemMultiplier;
                return (hcGrowth * 0.75 - stGrowth * 0.30) * 0.5 * weight;
            }
        }

        static class OpportunityCostFactor extends Factor {
            double constructionMultiplier, manufacturingMultiplier;
            OpportunityCostFactor(double w, double cm, double mm) {
                super(w, 1.0); constructionMultiplier = cm; manufacturingMultiplier = mm;
            }
            double calculateEffect(HistoricalData data, int year) {
                double mfg1980 = data.interpolate("jobGrowth", "manufacturing", 1980);
                double mfg2024 = data.interpolate("jobGrowth", "manufacturing", 2024);
                double con1980 = data.interpolate("jobGrowth", "construction", 1980);
                double con2024 = data.interpolate("jobGrowth", "construction", 2024);

                double mfgChange = ((mfg2024 - mfg1980) / mfg1980) * manufacturingMultiplier;
                double conChange = ((con2024 - con1980) / con1980) * constructionMultiplier;
                return -(mfgChange + conChange) * 0.1 * weight;
            }
        }

        static class CulturalPoliticalFactor extends Factor {
            CulturalPoliticalFactor(double w, double m) { super(w, m); }
            double calculateEffect(HistoricalData data, int year) {
                double growth = (data.interpolate("political", "femaleLiberalShare", 2024) -
                        data.interpolate("political", "femaleLiberalShare", 1980)) / 100.0;
                return growth * multiplier * 0.5 * weight;
            }
        }
    }
