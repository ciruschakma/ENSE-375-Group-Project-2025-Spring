package com.studyplanner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudyPlannerApp extends Application {
    public static Map<LocalDate, List<Task>> tasksByDate = new HashMap<>();

    public static List<Task> getAllOngoingTasks() {
        List<Task> result = new ArrayList<>();
        for (List<Task> dayTasks : tasksByDate.values()) {
            for (Task t : dayTasks) {
                if (!t.isCompleted()) result.add(t);
            }
        }
        return result;
    }

    public static List<Task> getAllCompletedTasks() {
        List<Task> result = new ArrayList<>();
        for (List<Task> dayTasks : tasksByDate.values()) {
            for (Task t : dayTasks) {
                if (t.isCompleted()) result.add(t);
            }
        }
        return result;
    }

    public static boolean addTask(String title,
                                  LocalDate date,
                                  String priority,
                                  int complexity) {
        if (title == null || title.isBlank()) return false;
        if (date.isBefore(LocalDate.now())) return false;
        List<String> valid = List.of("High", "Medium", "Low");
        if (!valid.contains(priority)) return false;

        Task t = new Task(title, date);
        t.setPriority(priority);
        t.setComplexity(complexity);
        tasksByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(t);
        TaskDB.saveTaskToDB(t);
        return true;
    }

    /** Returns the delay in seconds between now and the task's startTime. */
    public static long computeDelaySeconds(Task t) {
        return java.time.Duration.between(LocalDateTime.now(), t.getStartTime()).getSeconds();
    }

    private YearMonth currentYearMonth = YearMonth.now();
    private Label clockLabel = new Label();
    private Label monthLabel = new Label();
    private GridPane calendarGrid = new GridPane();
    private TaskListPage taskListPage;

    @Override
    public void start(Stage stage) {
        List<Task> loadedTasks = TaskDB.loadAllTasks();
        for (Task t : loadedTasks) {
            tasksByDate.computeIfAbsent(t.getDate(), d -> new ArrayList<>()).add(t);
        }
        for (Task t : loadedTasks) {
            if (t.isTimerEnabled()) scheduleTimer(t);
        }

        taskListPage = new TaskListPage();

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
            new Tab("Calendar", createCalendarPane()),
            new Tab("Tasks", taskListPage.createContent())
        );
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox root = new VBox(tabPane);
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Study Planner");
        stage.show();

        updateClock();
        Timeline clockTimeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1), e -> updateClock())
        );
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
        updateCalendar(currentYearMonth);
    }

    private void scheduleTimer(Task t) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = t.getStartTime();
        long delaySeconds = Duration.between(now, start).getSeconds();

        if (delaySeconds <= 0) {
            startCountdown(t);
        } else {
            Timeline startTimer = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(delaySeconds), e -> startCountdown(t))
            );
            startTimer.setCycleCount(1);
            startTimer.play();
        }
    }

    private void startCountdown(Task t) {
        Duration duration = t.getDuration();
        final long[] remaining = { duration.getSeconds() };

        Stage popup = new Stage();
        Label label = new Label();
        VBox vb = new VBox(10,
            new Label("Timer for: " + t.getTitle()),
            label
        );
        vb.setPadding(new Insets(20));
        popup.setScene(new Scene(vb));
        popup.setTitle("⏱ Timer");
        popup.show();

        Timeline countdown = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1), evt -> {
                if (remaining[0] <= 0) {
                    popup.close();
                    new Alert(Alert.AlertType.INFORMATION, "Time's up for: " + t.getTitle())
                        .showAndWait();
                } else {
                    long mins = remaining[0] / 60;
                    long secs = remaining[0] % 60;
                    label.setText(String.format("%02d:%02d", mins, secs));
                    remaining[0]--;
                }
            })
        );
        countdown.setCycleCount((int) duration.getSeconds() + 1);
        countdown.play();
    }

    private void updateClock() {
        clockLabel.setText(
            "Current Time: " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy  HH:mm:ss"))
        );
    }

    private VBox createCalendarPane() {
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

        StackPane calendarWrapper = new StackPane(calendarGrid);
        calendarWrapper.setStyle("-fx-padding: 10;");
        calendarGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        calendarWrapper.setAlignment(Pos.CENTER);
        clockLabel.setStyle("-fx-font-size: 16; -fx-padding: 10;");
        VBox mainLayout = new VBox(16, clockLabel, monthNav, calendarWrapper);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-padding: 20;");

        mainLayout.widthProperty().addListener((obs, oldW, newW) ->
            calendarWrapper.setPrefWidth(newW.doubleValue() * 0.5));
        mainLayout.heightProperty().addListener((obs, oldH, newH) ->
            calendarWrapper.setPrefHeight(newH.doubleValue() * 0.5));

        return mainLayout;
    }

    private void updateCalendar(YearMonth yearMonth) {
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);

        monthLabel.setText(
            yearMonth.getMonth().toString().substring(0,1).toUpperCase() +
            yearMonth.getMonth().toString().substring(1).toLowerCase() +
            " " + yearMonth.getYear()
        );

        calendarGrid.getColumnConstraints().clear();
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            cc.setHgrow(Priority.ALWAYS);
            calendarGrid.getColumnConstraints().add(cc);
        }

        calendarGrid.getRowConstraints().clear();
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int startCol = firstOfMonth.getDayOfWeek().getValue() - 1;
        int daysInMonth = yearMonth.lengthOfMonth();
        int totalCells = startCol + daysInMonth;
        int totalRows = 1 + (int)Math.ceil(totalCells / 7.0);

        for (int i = 0; i < totalRows; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / totalRows);
            rc.setVgrow(Priority.ALWAYS);
            calendarGrid.getRowConstraints().add(rc);
        }

        String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        for (int i = 0; i < days.length; i++) {
            Label header = new Label(days[i]);
            header.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
            header.setAlignment(Pos.CENTER);
            header.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            calendarGrid.add(header, i, 0);
            GridPane.setHgrow(header, Priority.ALWAYS);
            GridPane.setVgrow(header, Priority.ALWAYS);
        }

        int row = 1, col = startCol;
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate cellDate = yearMonth.atDay(day);

            Button cell = new Button(String.valueOf(day));
            cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
            cell.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: #ccc; " +
                "-fx-padding: 2; " +
                (cellDate.equals(LocalDate.now())
                    ? "-fx-background-color: #0078d7; -fx-text-fill: white; -fx-font-weight: bold;"
                    : "")
            );

            if (tasksByDate.containsKey(cellDate)) {
                cell.setStyle(cell.getStyle() +
                    "-fx-border-color: #00c853; -fx-border-width: 2;");
            }

            cell.setOnAction(e -> showTaskDialog(cellDate));

            calendarGrid.add(cell, col, row);
            GridPane.setHgrow(cell, Priority.ALWAYS);
            GridPane.setVgrow(cell, Priority.ALWAYS);

            if (++col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private void styleNavLabel(Label lbl) {
        lbl.setStyle("-fx-font-size: 18; -fx-cursor: hand;");
    }

    private void showTaskDialog(LocalDate date) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tasks for " + date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label existingLabel = new Label("Existing Tasks:");
        content.getChildren().add(existingLabel);
        List<Task> list = tasksByDate.getOrDefault(date, new ArrayList<>());
        for (Task task : list) {
            if (!task.isCompleted()) {
                content.getChildren().add(new Label(task.getTitle()));
            }
        }

        Label newLabel = new Label("Add a new task:");
        TextField input = new TextField();
        input.setPromptText("Task title");

        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("High", "Medium", "Low");
        priorityBox.setValue("Medium");

        Spinner<Integer> complexitySpinner = new Spinner<>(1, 10, 5);

        DatePicker startDatePicker = new DatePicker(date);
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, LocalDateTime.now().getHour());
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, LocalDateTime.now().getMinute());
        Spinner<Integer> durationSpinner = new Spinner<>(1, 240, 30);
        CheckBox enableTimer = new CheckBox("Enable timer");

        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            String title = input.getText().trim();
            if (!title.isEmpty()) {
                Task t = new Task(title, date);
                t.setPriority(priorityBox.getValue());
                t.setComplexity(complexitySpinner.getValue());

                LocalDateTime start = LocalDateTime.of(
                    startDatePicker.getValue(),
                    LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue())
                );
                t.setStartTime(start);
                t.setDuration(Duration.ofMinutes(durationSpinner.getValue()));
                t.setTimerEnabled(enableTimer.isSelected());

                tasksByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(t);
                TaskDB.saveTaskToDB(t);
                if (t.isTimerEnabled()) scheduleTimer(t);

                dialog.close();
                updateCalendar(currentYearMonth);
                taskListPage.refreshTaskLists();
            }
        });

        HBox addRow = new HBox(8, input, new Label("Priority:"), priorityBox,
            new Label("Complexity:"), complexitySpinner, addBtn);
        addRow.setAlignment(Pos.CENTER_LEFT);

        HBox timerRow1 = new HBox(8,
            new Label("Start:"), startDatePicker,
            new Label("Hour:"), hourSpinner,
            new Label("Min:"), minuteSpinner
        );
        timerRow1.setAlignment(Pos.CENTER_LEFT);

        HBox timerRow2 = new HBox(8,
            new Label("Duration (min):"), durationSpinner,
            enableTimer
        );
        timerRow2.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(newLabel, addRow,
            new Label("Timer Settings:"), timerRow1, timerRow2);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();

        taskListPage.refreshTaskLists();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
