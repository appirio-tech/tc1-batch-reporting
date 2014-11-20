SELECT SKIP @skip@ LIMIT @maxRow@
project_id as challenge_id,
component_name as challenge_name,
num_registrations,
num_submissions,
num_valid_submissions,
avg_raw_score,
avg_final_score,
phase_id,
phase_desc,
category_id,
category_desc,
posting_date,
submitby_date,
complete_date,
status_desc,
rating_date,
viewable_category_ind,
num_submissions_passed_review,
winner_id,
digital_run_ind,
project_category_id as challenge_category_id,
project_category_name as challenge_category_name,
contest_prizes_total as challenge_prizes_total,
duration,
fulfillment,
last_modification_date,
first_place_prize,
num_checkpoint_submissions,
num_valid_checkpoint_submissions,
(SELECT tpd.name FROM direct_project_dim tpd WHERE tpd.direct_project_id = p.tc_direct_project_id) as project_name,
(SELECT
    CASE
    WHEN tpd.project_status_id = 1
      THEN 'Active'
    WHEN tpd.project_status_id = 2
      THEN 'Inactive'
    WHEN tpd.project_status_id = 3
      THEN 'Cancelled'
    WHEN tpd.project_status_id = 4
      THEN 'Completed'
    ELSE
      'Draft'
    END
FROM direct_project_dim tpd WHERE tpd.direct_project_id = p.tc_direct_project_id
) as project_status,
(SELECT tpd.project_create_date FROM direct_project_dim tpd WHERE tpd.direct_project_id = p.tc_direct_project_id) as project_create_date,
(SELECT cpd.project_name FROM client_project_dim cpd WHERE cpd.client_project_id = p.client_project_id) as billing_account_name,
(SELECT cpd.cmc_account_id FROM client_project_dim cpd WHERE cpd.client_project_id = p.client_project_id) as cmc_account_id,
(SELECT cpd.customer_number FROM client_project_dim cpd WHERE cpd.client_project_id = p.client_project_id) as customer_number,
(SELECT cpd.subscription_number FROM client_project_dim cpd WHERE cpd.client_project_id = p.client_project_id) as subscription_number

FROM project p;