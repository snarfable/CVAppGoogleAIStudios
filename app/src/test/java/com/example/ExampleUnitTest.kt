package com.example

import com.example.model.CareerRepository
import com.example.model.RadioShowRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun verify_radio_transcript_data() {
    assertTrue(RadioShowRepository.transcriptLines.isNotEmpty())
    assertEquals(10, RadioShowRepository.transcriptLines.size)
    assertEquals(4, RadioShowRepository.speakers.size)
    assertTrue(RadioShowRepository.TOTAL_DURATION_SECONDS > 0)
    assertNotNull(RadioShowRepository.speakers["paul"])
    assertNotNull(RadioShowRepository.speakers["sarah"])
    assertNotNull(RadioShowRepository.speakers["leo"])
    assertNotNull(RadioShowRepository.speakers["mark"])
  }

  @Test
  fun verify_career_experience_data() {
    assertEquals("Ryan P. Daly", CareerRepository.profile["name"])
    assertEquals(5, CareerRepository.experiences.size)
    assertEquals(3, CareerRepository.keyProjects.size)
    assertEquals(4, CareerRepository.skillCategories.size)
    assertEquals(2, CareerRepository.education.size)
    assertEquals(4, CareerRepository.projectCategories.size)
    assertEquals(8, CareerRepository.topSkillsMetrics.size)
    val totalShare = CareerRepository.projectCategories.sumOf { it.experiencePercentage }
    assertEquals(100, totalShare)
  }
}

