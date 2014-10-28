SELECT SKIP @skip@ LIMIT @maxRow@
coder_id as user_id,
(SELECT state_name FROM state st WHERE st.state_code = c.state_code) as state_name,
(SELECT country_name FROM country co WHERE co.country_code = c.country_code) as country_name,
first_name,
middle_name,
last_name,
address1,
address2,
city,
zip,
activation_code,
member_since,
handle,
status,
email,
last_login,
image,
home_phone,
work_phone,
modify_date,
handle_lower,
(SELECT country_name FROM country co1 WHERE co1.country_code = c.comp_country_code) as competition_country_name,
(SELECT rating FROM algo_rating srmRating WHERE srmRating.algo_rating_type_id = 1 AND srmRating.coder_id = c.coder_id) as srm_rating,
(SELECT num_competitions FROM algo_rating srmRating WHERE srmRating.algo_rating_type_id = 1 AND srmRating.coder_id = c.coder_id) as srm_num_contests,
(SELECT cal.date FROM algo_rating srmRating, round r, calendar cal WHERE srmRating.algo_rating_type_id = 1 AND srmRating.coder_id = c.coder_id AND srmRating.last_rated_round_id = r.round_id and r.calendar_id = cal.calendar_id) as srm_last_competition_date,
(SELECT rating FROM algo_rating mmRating WHERE mmRating.algo_rating_type_id = 3 AND mmRating.coder_id = c.coder_id) as mm_rating,
(SELECT num_competitions FROM algo_rating mmRating WHERE mmRating.algo_rating_type_id = 3 AND mmRating.coder_id = c.coder_id) as mm_num_contests,
(SELECT cal.date FROM algo_rating mmRating, round r, calendar cal WHERE mmRating.algo_rating_type_id = 3 AND mmRating.coder_id = c.coder_id AND mmRating.last_rated_round_id = r.round_id and r.calendar_id = cal.calendar_id) as mm_last_competition_date,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 134) as conceptualization_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 134) as conceptualization_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 117) as specification_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 117) as specification_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 118) as architecture_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 118) as architecture_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 112) as component_design_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 112) as component_design_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 113) as component_development_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 113) as component_development_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 125) as assembly_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 125) as assembly_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 150) as code_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 150) as code_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 149) as F2F_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 149) as F2F_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 137) as test_scenario_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 137) as test_scenario_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 124) as test_script_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 124) as test_script_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 130) as ui_prototype_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 130) as ui_prototype_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 135) as ria_build_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 135) as ria_build_num_contests,

(SELECT rating from tcs_dw:user_rating ur where ur.user_id = c.coder_id and ur.phase_id = 146) as content_rating,
(SELECT ur.num_ratings FROM tcs_dw:user_rating ur WHERE ur.user_id = c.coder_id AND ur.phase_id = 146) as content_num_contests,

(SELECT SUM(total_amount) FROM user_payment WHERE user_id = c.coder_id) as total_earnings,
(SELECT SUM(up.total_amount) FROM user_payment up, payment p WHERE up.user_id = c.coder_id AND up.payment_id = p.payment_id AND p.payment_type_id IN (6, 10, 11, 12, 13, 21, 24, 29, 30, 31, 42, 43, 44, 49, 50, 51, 55, 60, 61, 64, 65) ) as total_prizes,
(SELECT SUM(up.total_amount) FROM user_payment up, payment p WHERE up.user_id = c.coder_id AND up.payment_id = p.payment_id AND p.payment_type_id IN (7, 26, 27, 28, 36, 38, 48) ) as total_review_earnings,
(SELECT SUM(up.total_amount) FROM user_payment up, payment p WHERE up.user_id = c.coder_id AND up.payment_id = p.payment_id AND p.payment_type_id IN (19, 22, 32, 33, 34) ) as total_tournament_earnings,
(SELECT SUM(up.total_amount) FROM user_payment up, payment p WHERE up.user_id = c.coder_id AND up.payment_id = p.payment_id AND p.payment_type_id IN (17, 18, 25, 40, 41, 63, 62) ) as total_dr_earnings,
(SELECT SUM(up.total_amount) FROM user_payment up, payment p WHERE up.user_id = c.coder_id AND up.payment_id = p.payment_id AND p.payment_type_id IN (45, 69) ) as total_copilot_earnings,
reg_source,
utm_source,
utm_medium,
utm_campaign,
create_date
FROM coder c ORDER BY coder_id
