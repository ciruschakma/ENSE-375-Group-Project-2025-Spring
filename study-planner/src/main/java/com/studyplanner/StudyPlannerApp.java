package com.studyplanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.geometry.*;
import javafx.scene.input.MouseButton;

public class StudyPlannerApp extends Application {
   public static Map<LocalDate, List<Task>> tasksByDate = new HashMap<>(); // made static/public for sharing

   // Helpers for ongoing/completed tasks
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

   private YearMonth currentYearMonth = YearMonth.now();
   private Label clockLabel = new Label();
   private Label monthLabel = new Label();
   private GridPane calendarGrid = new GridPane();
   private TaskListPage taskListPage; // Keep reference!

   @Override
   public void start(Stage stage) {
       // --- Load tasks from DB at startup ---
       List<Task> loadedTasks = TaskDB.loadAllTasks();
       for (Task t : loadedTasks) {
           tasksByDate
               .computeIfAbsent(t.getDate(), d -> new ArrayList<>())
               .add(t);
       }

       // Initialize
       taskListPage = new TaskListPage();

      TabPane tabPane = new TabPane();
       Tab calendarTab = new Tab("Calendar", createCalendarPane());
       Tab tasksTab = new Tab("Tasks", taskListPage.createContent());

      tabPane.getTabs().addAll(calendarTab, tasksTab);
       tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

       VBox root = new VBox(tabPane);
       Scene scene = new Scene(root, 900, 600);
       stage.setScene(scene);
       stage.setTitle("Study Planner");
      stage.show();

       updateClock();
       updateCalendar(currentYearMonth);
   }

   // Helper to style nav arrows
   private void styleNavLabel(Label lbl) {
       lbl.setStyle("-fx-font-size: 18; -fx-cursor: hand;");
   }

   // Update the clock label
   private void updateClock() {
       clockLabel.setText(
           "Current Time: " +
           java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy  HH:mm:ss"))
       );
   }

   // Calendar Pane: 50% of parent size, centered
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
       updateClock();
       VBox mainLayout = new VBox(16, clockLabel, monthNav, calendarWrapper);
       mainLayout.setAlignment(Pos.TOP_CENTER);
       mainLayout.setStyle("-fx-padding: 20;");

       // Bind calendar size to half the VBox size
       mainLayout.widthProperty().addListener((obs, oldW, newW) -> {
           calendarWrapper.setPrefWidth(newW.doubleValue() * 0.5);
       });
       mainLayout.heightProperty().addListener((obs, oldH, newH) -> {
           calendarWrapper.setPrefHeight(newH.doubleValue() * 0.5);
       });
       return mainLayout;
   }

  private void updateCalendar(YearMonth yearMonth) {
       calendarGrid.getChildren().clear();

       // Setup spacing
       calendarGrid.setHgap(5);
       calendarGrid.setVgap(5);

       // Set month label
       monthLabel.setText(
           yearMonth.getMonth().toString().substring(0,1).toUpperCase() +
           yearMonth.getMonth().toString().substring(1).toLowerCase() +
           " " + yearMonth.getYear()
       );

      // --- Column Constraints (spread 7 days) ---
       calendarGrid.getColumnConstraints().clear();
       for (int i = 0; i < 7; i++) {
           ColumnConstraints cc = new ColumnConstraints();
           cc.setPercentWidth(100.0 / 7);
           cc.setHgrow(Priority.ALWAYS);
           calendarGrid.getColumnConstraints().add(cc);
       }

       // --- Row Constraints (spread weeks) ---
       calendarGrid.getRowConstraints().clear();
       LocalDate firstOfMonth = yearMonth.atDay(1);
       int startCol = firstOfMonth.getDayOfWeek().getValue() - 1;  // Mon=1→col0
       int daysInMonth = yearMonth.lengthOfMonth();
       int totalCells = startCol + daysInMonth;
       int totalRows = 1 + (int)Math.ceil(totalCells / 7.0); // +1 for header row

       for (int i = 0; i < totalRows; i++) {
           RowConstraints rc = new RowConstraints();
           rc.setPercentHeight(100.0 / totalRows);
           rc.setVgrow(Priority.ALWAYS);
           calendarGrid.getRowConstraints().add(rc);
       }

       // --- Add Header Row ---
       String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
       for (int i = 0; i < days.length; i++) {
           Label dl = new Label(days[i]);
           dl.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
           dl.setAlignment(Pos.CENTER);
           dl.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
           calendarGrid.add(dl, i, 0);
           GridPane.setHgrow(dl, Priority.ALWAYS);
           GridPane.setVgrow(dl, Priority.ALWAYS);
       }

       // --- Fill Calendar Cells ---
       int row = 1, col = startCol;
       for (int day = 1; day <= daysInMonth; day++) {
           LocalDate cellDate = yearMonth.atDay(day);
           Label cell = new Label(String.valueOf(day));
           cell.setAlignment(Pos.CENTER);
           cell.setPadding(new Insets(2));
           cell.setStyle(
               (cellDate.equals(LocalDate.now())
                   ? "-fx-background-color:#0078d7; -fx-text-fill:white; -fx-font-weight:bold;"
                   : "-fx-border-color:#ccc;")
           );
           // Mark if tasks exist
           if (tasksByDate.containsKey(cellDate)) {
               cell.setStyle(cell.getStyle() +
                   "-fx-border-color:#00c853; -fx-border-width:2;");
           }
           // Cell size: let fill available slot
           cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
           cell.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

           // Click-to-add/view tasks
           cell.setOnMouseClicked(evt -> {
               if (evt.getButton() == MouseButton.PRIMARY) {
                   showTaskDialog(cellDate);
               }
           });
           calendarGrid.add(cell, col, row);
           GridPane.setHgrow(cell, Priority.ALWAYS);
           GridPane.setVgrow(cell, Priority.ALWAYS);

           if (++col == 7) {
               col = 0;
               row++;
           }
       }
   }
   private void showTaskDialog(LocalDate date) {
       Dialog<ButtonType> dialog = new Dialog<>();
       dialog.setTitle("Tasks for " + date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
       VBox content = new VBox(10);
       content.setPadding(new Insets(10));

       // Existing tasks
       Label existingLabel = new Label("Existing Tasks:");
       content.getChildren().add(existingLabel);
List<Task> list = tasksByDate.getOrDefault(date, new ArrayList<>());
for (Task task : list) {
  if (!task.isCompleted()) { // Only show ongoing tasks
       Label lbl = new Label(task.getTitle());
       content.getChildren().add(lbl);
   }
}

       // New task form (for now: just add a field for the title, but you can expand here)
       Label newLabel = new Label("Add a new task:");
       TextField input = new TextField();
       input.setPromptText("Task title");

       // Optional: ComboBox for priority
       ComboBox<String> priorityBox = new ComboBox<>();
       priorityBox.getItems().addAll("High", "Medium", "Low");
       priorityBox.setValue("Medium");

       // Optional: Spinner for complexity (1-10)
       Spinner<Integer> complexitySpinner = new Spinner<>(1, 10, 5);

       Button addBtn = new Button("Add");
       addBtn.setOnAction(e -> {
           String title = input.getText().trim();
           if (!title.isEmpty()) {
               Task t = new Task(title, date);
               t.setPriority(priorityBox.getValue());
               t.setComplexity(complexitySpinner.getValue());
               tasksByDate.computeIfAbsent(date, d -> new ArrayList<>()).add(t);
               TaskDB.saveTaskToDB(t);
               dialog.close();
               updateCalendar(currentYearMonth);
               if (taskListPage != null) taskListPage.refreshTaskLists();
           }
       });

       HBox addRow = new HBox(8, input, new Label("Priority:"), priorityBox, new Label("Complexity:"), complexitySpinner, addBtn);
       addRow.setAlignment(Pos.CENTER_LEFT);
       content.getChildren().addAll(newLabel, addRow);

       dialog.getDialogPane().setContent(content);
       dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
       dialog.showAndWait();

       if (taskListPage != null) taskListPage.refreshTaskLists();
   }

   public static void main(String[] args) {
       launch(args);
   }
}