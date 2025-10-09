package org.example.src.entities;

import lombok.Builder;
import lombok.Data;

import static org.example.src.utils.TreatLawyerParams.*;

@Data
public final class Lawyer {
    public String link;
    public String name;
    public String role;
    public String firm;
    public String country;
    public String practiceArea;
    public String email;
    public String phone;
    public String specialism;

    @Builder
    public Lawyer(String link, String name, String role, String firm, String country, String practiceArea, @org.jetbrains.annotations.NotNull String email, String phone) {
        this.link =         link.trim();
        this.role =         treatRole(role);
        this.firm =         firm.trim();
        this.country =      country.trim();
        this.practiceArea = treatPracticeArea(practiceArea);
        this.email =        treatEmail(email);
        this.phone =        treatPhone(phone);
        this.specialism =   treatSpecialism(this.role);

        // Move down so the email be treated and then used for the function `getNameFromEmail`
        this.name =         name.isEmpty() ? getNameFromEmail(this.email) : treatName(name);
    }
}
