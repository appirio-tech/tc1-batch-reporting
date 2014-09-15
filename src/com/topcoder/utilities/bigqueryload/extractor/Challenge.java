/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.utilities.bigqueryload.extractor;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * The challenge java bean to store the extracted challenge data.
 * </p>
 *
 * @author GreatKevin
 * @version 1.0
 */
public class Challenge {
    private Long challenge_id;
    private String challenge_name;
    private List<IdNamePair> challenge_platform_name;
    private List<IdNamePair> challenge_technology_name;
    private Long num_registrations;
    private Long num_submissions;
    private Long num_valid_submissions;
    private Double avg_raw_score;
    private Double avg_final_score;
    private Long phase_id;
    private String phase_desc;
    private Long category_id;
    private String category_desc;
    private Date posting_date;
    private Date submitby_date;
    private Date complete_date;
    private String status_desc;
    private Date rating_date;
    private Long viewable_category_ind;
    private Long num_submissions_passed_review;
    private Long winner_id;
    private Long digital_run_ind;
    private Long challenge_category_id;
    private String challenge_category_name;
    private Double challenge_prizes_total;
    private Double duration;
    private Double fulfillment;
    private Date last_modification_date;
    private Double first_place_prize;
    private Long num_checkpoint_submissions;
    private Long num_valid_checkpoint_submissions;
    private String project_name;
    private String project_status;
    private Date project_create_date;
    private String billing_account_name;
    private String client_name;
    private String cmc_account_id;
    private String customer_number;
    private String subscription_number;

    public Long getChallenge_id() {
        return challenge_id;
    }

    public void setChallenge_id(Long challenge_id) {
        this.challenge_id = challenge_id;
    }

    public String getChallenge_name() {
        return challenge_name;
    }

    public void setChallenge_name(String challenge_name) {
        this.challenge_name = challenge_name;
    }

    public List<IdNamePair> getChallenge_platform_name() {
        return challenge_platform_name;
    }

    public void setChallenge_platform_name(List<IdNamePair> challenge_platform_name) {
        this.challenge_platform_name = challenge_platform_name;
    }

    public List<IdNamePair> getChallenge_technology_name() {
        return challenge_technology_name;
    }

    public void setChallenge_technology_name(List<IdNamePair> challenge_technology_name) {
        this.challenge_technology_name = challenge_technology_name;
    }

    public Long getNum_registrations() {
        return num_registrations;
    }

    public void setNum_registrations(Long num_registrations) {
        this.num_registrations = num_registrations;
    }

    public Long getNum_submissions() {
        return num_submissions;
    }

    public void setNum_submissions(Long num_submissions) {
        this.num_submissions = num_submissions;
    }

    public Long getNum_valid_submissions() {
        return num_valid_submissions;
    }

    public void setNum_valid_submissions(Long num_valid_submissions) {
        this.num_valid_submissions = num_valid_submissions;
    }

    public Double getAvg_raw_score() {
        return avg_raw_score;
    }

    public void setAvg_raw_score(Double avg_raw_score) {
        this.avg_raw_score = avg_raw_score;
    }

    public Double getAvg_final_score() {
        return avg_final_score;
    }

    public void setAvg_final_score(Double avg_final_score) {
        this.avg_final_score = avg_final_score;
    }

    public Long getPhase_id() {
        return phase_id;
    }

    public void setPhase_id(Long phase_id) {
        this.phase_id = phase_id;
    }

    public String getPhase_desc() {
        return phase_desc;
    }

    public void setPhase_desc(String phase_desc) {
        this.phase_desc = phase_desc;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public String getCategory_desc() {
        return category_desc;
    }

    public void setCategory_desc(String category_desc) {
        this.category_desc = category_desc;
    }

    public Date getPosting_date() {
        return posting_date;
    }

    public void setPosting_date(Date posting_date) {
        this.posting_date = posting_date;
    }

    public Date getSubmitby_date() {
        return submitby_date;
    }

    public void setSubmitby_date(Date submitby_date) {
        this.submitby_date = submitby_date;
    }

    public Date getComplete_date() {
        return complete_date;
    }

    public void setComplete_date(Date complete_date) {
        this.complete_date = complete_date;
    }

    public String getStatus_desc() {
        return status_desc;
    }

    public void setStatus_desc(String status_desc) {
        this.status_desc = status_desc;
    }

    public Date getRating_date() {
        return rating_date;
    }

    public void setRating_date(Date rating_date) {
        this.rating_date = rating_date;
    }

    public Long getViewable_category_ind() {
        return viewable_category_ind;
    }

    public void setViewable_category_ind(Long viewable_category_ind) {
        this.viewable_category_ind = viewable_category_ind;
    }

    public Long getNum_submissions_passed_review() {
        return num_submissions_passed_review;
    }

    public void setNum_submissions_passed_review(Long num_submissions_passed_review) {
        this.num_submissions_passed_review = num_submissions_passed_review;
    }

    public Long getWinner_id() {
        return winner_id;
    }

    public void setWinner_id(Long winner_id) {
        this.winner_id = winner_id;
    }

    public Long getDigital_run_ind() {
        return digital_run_ind;
    }

    public void setDigital_run_ind(Long digital_run_ind) {
        this.digital_run_ind = digital_run_ind;
    }

    public Long getChallenge_category_id() {
        return challenge_category_id;
    }

    public void setChallenge_category_id(Long challenge_category_id) {
        this.challenge_category_id = challenge_category_id;
    }

    public String getChallenge_category_name() {
        return challenge_category_name;
    }

    public void setChallenge_category_name(String challenge_category_name) {
        this.challenge_category_name = challenge_category_name;
    }

    public Double getChallenge_prizes_total() {
        return challenge_prizes_total;
    }

    public void setChallenge_prizes_total(Double challenge_prizes_total) {
        this.challenge_prizes_total = challenge_prizes_total;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(Double fulfillment) {
        this.fulfillment = fulfillment;
    }

    public Date getLast_modification_date() {
        return last_modification_date;
    }

    public void setLast_modification_date(Date last_modification_date) {
        this.last_modification_date = last_modification_date;
    }

    public Double getFirst_place_prize() {
        return first_place_prize;
    }

    public void setFirst_place_prize(Double first_place_prize) {
        this.first_place_prize = first_place_prize;
    }

    public Long getNum_checkpoint_submissions() {
        return num_checkpoint_submissions;
    }

    public void setNum_checkpoint_submissions(Long num_checkpoint_submissions) {
        this.num_checkpoint_submissions = num_checkpoint_submissions;
    }

    public Long getNum_valid_checkpoint_submissions() {
        return num_valid_checkpoint_submissions;
    }

    public void setNum_valid_checkpoint_submissions(Long num_valid_checkpoint_submissions) {
        this.num_valid_checkpoint_submissions = num_valid_checkpoint_submissions;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_status() {
        return project_status;
    }

    public void setProject_status(String project_status) {
        this.project_status = project_status;
    }

    public Date getProject_create_date() {
        return project_create_date;
    }

    public void setProject_create_date(Date project_create_date) {
        this.project_create_date = project_create_date;
    }

    public String getBilling_account_name() {
        return billing_account_name;
    }

    public void setBilling_account_name(String billing_account_name) {
        this.billing_account_name = billing_account_name;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getCmc_account_id() {
        return cmc_account_id;
    }

    public void setCmc_account_id(String cmc_account_id) {
        this.cmc_account_id = cmc_account_id;
    }

    public String getCustomer_number() {
        return customer_number;
    }

    public void setCustomer_number(String customer_number) {
        this.customer_number = customer_number;
    }

    public String getSubscription_number() {
        return subscription_number;
    }

    public void setSubscription_number(String subscription_number) {
        this.subscription_number = subscription_number;
    }
}
