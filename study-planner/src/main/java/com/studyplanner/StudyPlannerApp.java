package com.studyplanner;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StudyPlannerApp extends Application {
    private Label clockLabel = new Label();
    private GridPane calendarGrid = new GridPane();
    private Label monthLabel = new Label();
    private YearMonth currentYearMonth = YearMonth.now();

    // In-memory task storage
    private Map<LocalDate, List<Task>> tasksByDate = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        // 1) Real-time clock
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // 2) Calendar initial render
        updateCalendar(currentYearMonth);

        // 3) Month navigation controls
        Label prev = new Label("<");
        styleNavLabel(prev);
        prev.setOnMouseClicked(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar(currentYearMonth);
        });

        Label next = new Label(">");
        styleNavLabel(next);
        next.setOnMouseClicked(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar(currentYearMonth);
        });

        HBox monthNav = new HBox(10, prev, monthLabel, next);
        monthNav.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(16, clockLabel, monthNav, calendarGrid);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(mainLayout, 500, 420);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smart Study Planner");
        primaryStage.show();
    }

    // Helper to style nav arrows
    private void styleNavLabel(Label lbl) {
        lbl.setStyle("-fx-font-size: 18; -fx-cursor: hand;");
    }

    // Update the clock label
    private void updateClock() {
        clockLabel.setText(
            "Current Time: " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy  HH:mm:ss"))
        );
    }

    // Build month calendar
    private void updateCalendar(YearMonth yearMonth) {
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);

        // Month title
        monthLabel.setText(
            yearMonth.getMonth().toString().substring(0,1).toUpperCase() +
            yearMonth.getMonth().toString().substring(1).toLowerCase() +
            " " + yearMonth.getYear()
        );

        // Day-of-week headers
        String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        for (int i=0; i<days.length; i++) {
            Label dl = new Label(days[i]);
            dl.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
            calendarGrid.add(dl, i, 0);
        }

        // Populate day cells
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int startCol = firstOfMonth.getDayOfWeek().getValue() - 1;  // Mon=1→col0
        int daysInMonth = yearMonth.lengthOfMonth();
        int row=1, col=startCol;

        for (int day=1; day<=daysInMonth; day++) {
            LocalDate cellDate = yearMonth.atDay(day);
            Label cell = new Label(String.valueOf(day));
            cell.setMinSize(45, 35);
            cell.setAlignment(Pos.CENTER);
            cell.setPadding(new Insets(2));
            cell.setStyle(
                (cellDate.equals(LocalDate.now())
                    ? "-fx-background-color:#0078d7; -fx-text-fill:white; -fx-font-weight:bold; "
                    : "-fx-border-color:#ccc;")
            );

            // Mark if tasks exist
            if (tasksByDate.containsKey(cellDate)) {
                cell.setStyle(cell.getStyle() +
                    "-fx-border-color:#00c853; -fx-border-width:2;");
            }

            // Click-to-add/view tasks
            cell.setOnMouseClicked(evt -> {
                if (evt.getButton() == MouseButton.PRIMARY) {
                    showTaskDialog(cellDate);
                }
            });

            calendarGrid.add(cell, col, row);
            if (++col == 7) {
                col = 0;
                row++;
            }
        }
    }

    // Dialog to view existing & add new tasks
    private void showTaskDialog(LocalDate date) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tasks for " + date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Existing tasks
        Label existingLabel = new Label("Existing Tasks:");
        content.getChildren().add(existingLabel);
        List<Task> list = tasksByDate.getOrDefault(date, new ArrayList<>());
        list.forEach(task -> {
            CheckBox cb = new CheckBox(task.getTitle());
            cb.setSelected(task.isCompleted());
            cb.setOnAction(e -> task.setCompleted(cb.isSelected()));
            content.getChildren().add(cb);
        });

        // New task form
        Label newLabel = new Label("Add a new task:");
        TextField input = new TextField();
        input.setPromptText("Task title");
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            String title = input.getText().trim();
            if (!title.isEmpty()) {
                Task t = new Task(title, date);
                tasksByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(t);
                dialog.close();
                updateCalendar(currentYearMonth);
            }
        });

        HBox addRow = new HBox(8, input, addBtn);
        content.getChildren().addAll(newLabel, addRow);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
