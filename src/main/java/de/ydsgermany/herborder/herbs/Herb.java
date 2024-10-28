package de.ydsgermany.herborder.herbs;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "herbs")
public class Herb {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "herbs_generator")
    @SequenceGenerator(name = "herbs_generator", sequenceName = "herbs_seq", allocationSize = 1)
    Long id;

    String name;

}
