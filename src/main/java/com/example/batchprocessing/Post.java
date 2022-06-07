package com.example.batchprocessing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

  Integer id;
  Integer userId;
  String title;
  String completed;

  @Override
  public String toString() {
    return "title: " + title + ", completed: " + completed;
  }

}