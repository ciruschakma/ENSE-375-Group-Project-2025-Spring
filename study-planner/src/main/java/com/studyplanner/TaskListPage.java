// src/main/java/com/studyplanner/TaskListPage.java
package com.studyplanner;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class TaskListPage {

   private TableView<Task> ongoingTable = new TableView<>();
   private TableView<Task> completedTable = new TableView<>();

   // Controls for filtering/sorting
   private ComboBox<String> priorityFilter = new ComboBox<>();
   private ComboBox<String> sortByFilter = new ComboBox<>();

   // Data for tables
   private ObservableList<Task> ongoingTasks;
   private FilteredList<Task> filtered;
   private SortedList<Task> sorted;

   public Pane createContent() {
       // --- Ongoing Tasks Table ---
       TableColumn<Task, String> taskCol = new TableColumn<>("Task");
       taskCol.setCellValueFactory(new PropertyValueFactory<>("title"));

       TableColumn<Task, String> dueDateCol = new TableColumn<>("Due Date");
       dueDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
           cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
       ));

       TableColumn<Task, String> priorityCol = new TableColumn<>("Priority");
       priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));

       TableColumn<Task, Integer> complexityCol = new TableColumn<>("Complexity");
       complexityCol.setCellValueFactory(new PropertyValueFactory<>("complexity"));

       TableColumn<Task, String> categoryCol = new TableColumn<>("Category");
       categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

       TableColumn<Task, String> notesCol = new TableColumn<>("Notes");
       notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

       TableColumn<Task, String> createdAtCol = new TableColumn<>("Created At");
       createdAtCol.setCellValueFactory(cellData -> new SimpleStringProperty(
           cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
       ));

       // Optional timer columns
       TableColumn<Task, String> startCol = new TableColumn<>("Starts At");
       startCol.setCellValueFactory(cellData -> new SimpleStringProperty(
           cellData.getValue().getStartTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
       ));

       TableColumn<Task, Integer> durCol = new TableColumn<>("Duration (min)");
       durCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(
           (int)cellData.getValue().getDuration().toMinutes()
       ).asObject());

       ongoingTable.getColumns().setAll(
           taskCol, dueDateCol, priorityCol, complexityCol,
           categoryCol, notesCol, createdAtCol,
           startCol, durCol
       );
       ongoingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

       // --- Context Menu: Mark as Completed ---
       ongoingTable.setRowFactory(tv -> {
           TableRow<Task> row = new TableRow<>();
           ContextMenu menu = new ContextMenu();
           MenuItem completeItem = new MenuItem("Mark as Completed");
           completeItem.setOnAction(e -> {
               Task t = row.getItem();
               if (t != null) {
                   t.setCompleted(true);
                   TaskDB.updateTaskInDB(t);
                   refreshTaskLists();
               }
           });
           menu.getItems().add(completeItem);
           row.contextMenuProperty().bind(
               javafx.beans.binding.Bindings
               .when(row.emptyProperty())
               .then((ContextMenu) null)
               .otherwise(menu)
           );
           return row;
       });

       // --- Filter and sort controls ---
       priorityFilter.getItems().addAll("All", "High", "Medium", "Low");
       priorityFilter.setValue("All");

       sortByFilter.getItems().addAll("Due Date", "Priority", "Complexity");
       sortByFilter.setValue("Due Date");

       HBox filters = new HBox(10, new Label("Priority:"), priorityFilter, new Label("Sort by:"), sortByFilter);
       filters.setPadding(new Insets(10));
       filters.setAlignment(Pos.CENTER_LEFT);

       // --- Data/Logic: FilteredList/SortedList ---
       ongoingTasks = FXCollections.observableArrayList(StudyPlannerApp.getAllOngoingTasks());
       filtered = new FilteredList<>(ongoingTasks, t -> true);
       sorted = new SortedList<>(filtered);
       ongoingTable.setItems(sorted);

       // Default sorting
       sorted.setComparator(Comparator.comparing(Task::getDate));

       // Priority filter action
       priorityFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
           filtered.setPredicate(task -> {
               if (newVal.equals("All")) return true;
               return task.getPriority().equalsIgnoreCase(newVal);
           });
       });
       // Sort by
       sortByFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
           if (newVal.equals("Due Date")) {
               sorted.setComparator(Comparator.comparing(Task::getDate));
           } else if (newVal.equals("Priority")) {
               List<String> order = Arrays.asList("High", "Medium", "Low");
               sorted.setComparator(Comparator.comparingInt(task -> order.indexOf(task.getPriority())));
           } else if (newVal.equals("Complexity")) {
               sorted.setComparator(Comparator.comparingInt(Task::getComplexity));
           }
       });

       // --- Completed Tasks Table ---
       TableColumn<Task, String> cTaskCol = new TableColumn<>("Task");
       cTaskCol.setCellValueFactory(new PropertyValueFactory<>("title"));

       TableColumn<Task, String> cDueDateCol = new TableColumn<>("Due Date");
       cDueDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
           cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
       ));

       completedTable.getColumns().setAll(cTaskCol, cDueDateCol);
       completedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       completedTable.setItems(FXCollections.observableArrayList(StudyPlannerApp.getAllCompletedTasks()));

       // --- Layout ---
       VBox ongoingBox = new VBox(
           new Label("Ongoing Tasks:"),
           filters,
           ongoingTable
       );
       ongoingBox.setSpacing(5);
       ongoingBox.setPadding(new Insets(5));

       VBox completedBox = new VBox(
           new Label("Completed Tasks:"),
           completedTable
       );
       completedBox.setSpacing(5);
       completedBox.setPadding(new Insets(5));

       VBox root = new VBox(ongoingBox, completedBox);
       root.setSpacing(20);

       return root;
   }

   // Call this when data changes to refresh tables
   public void refreshTaskLists() {
       ongoingTasks.setAll(StudyPlannerApp.getAllOngoingTasks());
       completedTable.setItems(FXCollections.observableArrayList(StudyPlannerApp.getAllCompletedTasks()));
   }
}
