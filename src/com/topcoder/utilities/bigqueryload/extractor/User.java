/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload.extractor;

import java.util.Date;

/**
 * <p>
 *     The user java bean to store the extracted user data.
 * </p>
 *
 * <p>
 * Version 1.1 (TopCoder - Update DW loading of Coder and Update Big Query User Extractor)
 * @author Veve
 * @challenge 30045984
 * <ul>
 *  Add columns
 *      - reg_source
 *      - utm_source
 *      - utm_medium
 *      - utm_campaign
 *      - create_date
 * </ul>
 * </p>
 *
 * @author GreatKevin, Veve
 * @version 1.1
 */
public class User  {
    private Long user_id;
    private String first_name;
    private String middle_name;
    private String last_name;
    private String state_name;
    private String country_name;
    private String address1;
    private String address2;
    private String city;
    private String zip;
    private String activation_code;
    private Date member_since;
    private String handle;
    private String email;
    private String status;
    private Date last_login;
    private Long image;
    private String home_phone;
    private String work_phone;
    private Date modify_date;
    private String handle_lower;
    private String competition_country_name;
    private Long srm_rating;
    private Long srm_num_contests;
    private Long mm_rating;
    private Long mm_num_contests;
    private Date srm_last_competition_date;
    private Date mm_last_competition_date;
    private Long conceptualization_rating;
    private Long conceptualization_num_contests;
    private Long specification_rating;
    private Long specification_num_contests;
    private Long architecture_rating;
    private Long architecture_num_contests;
    private Long component_design_rating;
    private Long component_design_num_contests;
    private Long component_development_rating;
    private Long component_development_num_contests;
    private Long assembly_rating;
    private Long assembly_num_contests;
    private Long code_rating;
    private Long code_num_contests;
    private Long F2F_rating;
    private Long F2F_num_contests;
    private Long test_scenario_rating;
    private Long test_scenario_num_contests;
    private Long test_script_rating;
    private Long test_script_num_contests;
    private Long ui_prototype_rating;
    private Long ui_prototype_num_contests;
    private Long ria_build_rating;
    private Long ria_build_num_contests;
    private Long content_rating;
    private Long content_num_contests;
    private Double total_earnings;
    private Double total_prizes;
    private Double total_review_earnings;
    private Double total_tournament_earnings;
    private Double total_dr_earnings;
    private Double total_copilot_earnings;
    private String reg_source;
    private String utm_source;
    private String utm_medium;
    private String utm_campaign;
    private Date create_date;


    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getActivation_code() {
        return activation_code;
    }

    public void setActivation_code(String activation_code) {
        this.activation_code = activation_code;
    }

    public Date getMember_since() {
        return member_since;
    }

    public void setMember_since(Date member_since) {
        this.member_since = member_since;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLast_login() {
        return last_login;
    }

    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }

    public Long getImage() {
        return image;
    }

    public void setImage(Long image) {
        this.image = image;
    }

    public String getCompetition_country_name() {
        return competition_country_name;
    }

    public void setCompetition_country_name(String competition_country_name) {
        this.competition_country_name = competition_country_name;
    }

    public Long getSrm_rating() {
        return srm_rating;
    }

    public void setSrm_rating(Long srm_rating) {
        this.srm_rating = srm_rating;
    }

    public Long getSrm_num_contests() {
        return srm_num_contests;
    }

    public void setSrm_num_contests(Long srm_num_contests) {
        this.srm_num_contests = srm_num_contests;
    }

    public Long getMm_rating() {
        return mm_rating;
    }

    public void setMm_rating(Long mm_rating) {
        this.mm_rating = mm_rating;
    }

    public Long getMm_num_contests() {
        return mm_num_contests;
    }

    public void setMm_num_contests(Long mm_num_contests) {
        this.mm_num_contests = mm_num_contests;
    }

    public Date getSrm_last_competition_date() {
        return srm_last_competition_date;
    }

    public void setSrm_last_competition_date(Date srm_last_competition_date) {
        this.srm_last_competition_date = srm_last_competition_date;
    }

    public Date getMm_last_competition_date() {
        return mm_last_competition_date;
    }

    public void setMm_last_competition_date(Date mm_last_competition_date) {
        this.mm_last_competition_date = mm_last_competition_date;
    }

    public Long getConceptualization_rating() {
        return conceptualization_rating;
    }

    public void setConceptualization_rating(Long conceptualization_rating) {
        this.conceptualization_rating = conceptualization_rating;
    }

    public Long getConceptualization_num_contests() {
        return conceptualization_num_contests;
    }

    public void setConceptualization_num_contests(Long conceptualization_num_contests) {
        this.conceptualization_num_contests = conceptualization_num_contests;
    }

    public Long getSpecification_rating() {
        return specification_rating;
    }

    public void setSpecification_rating(Long specification_rating) {
        this.specification_rating = specification_rating;
    }

    public Long getSpecification_num_contests() {
        return specification_num_contests;
    }

    public void setSpecification_num_contests(Long specification_num_contests) {
        this.specification_num_contests = specification_num_contests;
    }

    public Long getArchitecture_rating() {
        return architecture_rating;
    }

    public void setArchitecture_rating(Long architecture_rating) {
        this.architecture_rating = architecture_rating;
    }

    public Long getArchitecture_num_contests() {
        return architecture_num_contests;
    }

    public void setArchitecture_num_contests(Long architecture_num_contests) {
        this.architecture_num_contests = architecture_num_contests;
    }

    public Long getComponent_design_rating() {
        return component_design_rating;
    }

    public void setComponent_design_rating(Long component_design_rating) {
        this.component_design_rating = component_design_rating;
    }

    public Long getComponent_design_num_contests() {
        return component_design_num_contests;
    }

    public void setComponent_design_num_contests(Long component_design_num_contests) {
        this.component_design_num_contests = component_design_num_contests;
    }

    public Long getComponent_development_rating() {
        return component_development_rating;
    }

    public void setComponent_development_rating(Long component_development_rating) {
        this.component_development_rating = component_development_rating;
    }

    public Long getComponent_development_num_contests() {
        return component_development_num_contests;
    }

    public void setComponent_development_num_contests(Long component_development_num_contests) {
        this.component_development_num_contests = component_development_num_contests;
    }

    public Long getAssembly_rating() {
        return assembly_rating;
    }

    public void setAssembly_rating(Long assembly_rating) {
        this.assembly_rating = assembly_rating;
    }

    public Long getAssembly_num_contests() {
        return assembly_num_contests;
    }

    public void setAssembly_num_contests(Long assembly_num_contests) {
        this.assembly_num_contests = assembly_num_contests;
    }

    public Long getCode_rating() {
        return code_rating;
    }

    public void setCode_rating(Long code_rating) {
        this.code_rating = code_rating;
    }

    public Long getCode_num_contests() {
        return code_num_contests;
    }

    public void setCode_num_contests(Long code_num_contests) {
        this.code_num_contests = code_num_contests;
    }

    public Long getF2F_rating() {
        return F2F_rating;
    }

    public void setF2F_rating(Long f2F_rating) {
        F2F_rating = f2F_rating;
    }

    public Long getF2F_num_contests() {
        return F2F_num_contests;
    }

    public void setF2F_num_contests(Long f2F_num_contests) {
        F2F_num_contests = f2F_num_contests;
    }

    public Long getTest_scenario_rating() {
        return test_scenario_rating;
    }

    public void setTest_scenario_rating(Long test_scenario_rating) {
        this.test_scenario_rating = test_scenario_rating;
    }

    public Long getTest_scenario_num_contests() {
        return test_scenario_num_contests;
    }

    public void setTest_scenario_num_contests(Long test_scenario_num_contests) {
        this.test_scenario_num_contests = test_scenario_num_contests;
    }

    public Long getTest_script_rating() {
        return test_script_rating;
    }

    public void setTest_script_rating(Long test_script_rating) {
        this.test_script_rating = test_script_rating;
    }

    public Long getTest_script_num_contests() {
        return test_script_num_contests;
    }

    public void setTest_script_num_contests(Long test_script_num_contests) {
        this.test_script_num_contests = test_script_num_contests;
    }

    public Long getUi_prototype_rating() {
        return ui_prototype_rating;
    }

    public void setUi_prototype_rating(Long ui_prototype_rating) {
        this.ui_prototype_rating = ui_prototype_rating;
    }

    public Long getUi_prototype_num_contests() {
        return ui_prototype_num_contests;
    }

    public void setUi_prototype_num_contests(Long ui_prototype_num_contests) {
        this.ui_prototype_num_contests = ui_prototype_num_contests;
    }

    public Long getRia_build_rating() {
        return ria_build_rating;
    }

    public void setRia_build_rating(Long ria_build_rating) {
        this.ria_build_rating = ria_build_rating;
    }

    public Long getRia_build_num_contests() {
        return ria_build_num_contests;
    }

    public void setRia_build_num_contests(Long ria_build_num_contests) {
        this.ria_build_num_contests = ria_build_num_contests;
    }

    public Long getContent_rating() {
        return content_rating;
    }

    public void setContent_rating(Long content_rating) {
        this.content_rating = content_rating;
    }

    public Long getContent_num_contests() {
        return content_num_contests;
    }

    public void setContent_num_contests(Long content_num_contests) {
        this.content_num_contests = content_num_contests;
    }

    public Double getTotal_earnings() {
        return total_earnings;
    }

    public void setTotal_earnings(Double total_earnings) {
        this.total_earnings = total_earnings;
    }

    public Double getTotal_prizes() {
        return total_prizes;
    }

    public void setTotal_prizes(Double total_prizes) {
        this.total_prizes = total_prizes;
    }

    public Double getTotal_review_earnings() {
        return total_review_earnings;
    }

    public void setTotal_review_earnings(Double total_review_earnings) {
        this.total_review_earnings = total_review_earnings;
    }

    public Double getTotal_tournament_earnings() {
        return total_tournament_earnings;
    }

    public void setTotal_tournament_earnings(Double total_tournament_earnings) {
        this.total_tournament_earnings = total_tournament_earnings;
    }

    public Double getTotal_dr_earnings() {
        return total_dr_earnings;
    }

    public void setTotal_dr_earnings(Double total_dr_earnings) {
        this.total_dr_earnings = total_dr_earnings;
    }

    public Double getTotal_copilot_earnings() {
        return total_copilot_earnings;
    }

    public void setTotal_copilot_earnings(Double total_copilot_earnings) {
        this.total_copilot_earnings = total_copilot_earnings;
    }

    public String getHome_phone() {
        return home_phone;
    }

    public void setHome_phone(String home_phone) {
        this.home_phone = home_phone;
    }

    public String getWork_phone() {
        return work_phone;
    }

    public void setWork_phone(String work_phone) {
        this.work_phone = work_phone;
    }

    public Date getModify_date() {
        return modify_date;
    }

    public void setModify_date(Date modify_date) {
        this.modify_date = modify_date;
    }

    public String getHandle_lower() {
        return handle_lower;
    }

    public void setHandle_lower(String handle_lower) {
        this.handle_lower = handle_lower;
    }

    public String getReg_source() {
        return reg_source;
    }

    public void setReg_source(String reg_source) {
        this.reg_source = reg_source;
    }

    public String getUtm_source() {
        return utm_source;
    }

    public void setUtm_source(String utm_source) {
        this.utm_source = utm_source;
    }

    public String getUtm_medium() {
        return utm_medium;
    }

    public void setUtm_medium(String utm_medium) {
        this.utm_medium = utm_medium;
    }

    public String getUtm_campaign() {
        return utm_campaign;
    }

    public void setUtm_campaign(String utm_campaign) {
        this.utm_campaign = utm_campaign;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }
}
