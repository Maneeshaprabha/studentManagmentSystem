package com.sms.deastudentmanagementsystem.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.sms.deastudentmanagementsystem.constant.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sms.deastudentmanagementsystem.exception.ResourceNotFoundException;
import com.sms.deastudentmanagementsystem.model.Assignment;
import com.sms.deastudentmanagementsystem.repository.AssignmentRepository;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @GetMapping
    public List<Assignment> getAllAssignment(){
        return assignmentRepository.findAll();
    }

    @PostMapping
    public Assignment createAssignment(@RequestBody Assignment assignment){
        return assignmentRepository.save(assignment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));
        return ResponseEntity.ok(assignment);
    }

//    @PostMapping("/upload")
//    public ResponseEntity<Assignment> uploadFile(@RequestParam("file") MultipartFile file,
//                                                 @RequestParam("subject") String subject,
//                                                 @RequestParam("studentName") String studentName,
//                                                 @RequestParam("description") String description) throws IOException {
//        Assignment assignment = new Assignment();
//        assignment.setSubject(subject);
//        assignment.setStudentName(studentName);
//        assignment.setDescription(description);
//        assignment.setAssignments(file.getBytes());
//
//        Assignment savedAssignment = assignmentRepository.save(assignment);
//        return new ResponseEntity<>(savedAssignment, HttpStatus.CREATED);
//    }

    @PostMapping("/upload")
    public ResponseEntity<Assignment> uploadFile(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("subject") String subject,
                                                 @RequestParam("studentName") String studentName,
                                                 @RequestParam("description") String description) throws IOException {
        // Creating the directory to store file
        String directoryPath = AppConstant.directoryPath; // You need to replace this path
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Creating the file on server
        String fileName = file.getOriginalFilename(); // You might want to set a unique name
        Path filePath = Paths.get(directoryPath, fileName);
        file.transferTo(filePath);
        // Save the file path in the database instead of the bytes
        Assignment assignment = new Assignment();
        assignment.setSubject(subject);
        assignment.setStudentName(studentName);
        assignment.setDescription(description);
        //assignment.setAssignments(file.getBytes());
        assignment.setFilePath(AppConstant.fileFullPath+ fileName);
        assignmentRepository.save(assignment);

        return ResponseEntity.ok(assignment);
    }



    @PutMapping("/{id}")
    public ResponseEntity<Assignment> updateAssignment(@PathVariable long id, @RequestBody Assignment assignmentDetails) {
        Assignment updateAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not exist with id: " + id));

        updateAssignment.setStudentName(assignmentDetails.getStudentName());
        updateAssignment.setDescription(assignmentDetails.getDescription());
        updateAssignment.setSubject(assignmentDetails.getSubject());
        updateAssignment.setAssignments(assignmentDetails.getAssignments());

        assignmentRepository.save(updateAssignment);

        return ResponseEntity.ok(updateAssignment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable long id){
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not exist with id: " + id));
        assignmentRepository.delete(assignment);
        return ResponseEntity.noContent().build();
    }
}
