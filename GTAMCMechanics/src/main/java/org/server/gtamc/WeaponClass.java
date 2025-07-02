package org.server.gtamc;

import java.util.List;


public class WeaponClass {


    private Root root;

    // Getter and Setter for Olympia


    public Root getRoot() {
        return root;
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    // Inner class to represent Olympia in the YAML structure
    public static class Root {
        private Info info;
        private Skin skin;
        private String projectile;
        private Shoot shoot;
        private Reload reload;
        private Damage damage;
        private Firearm_action firearm_action;
        private Scope scope;

        // Getter and Setter for Info
        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        public Skin getSkin() {
            return skin;
        }

        public void setSkin(Skin skin) {
            this.skin = skin;
        }
        public String getProjectile() {return projectile;}
        public void setProjectile(String projectile) {this.projectile = projectile;}
        public Shoot getShoot() {return shoot;}
        public void setShoot(Shoot shoot) {this.shoot = shoot;}
        public Reload getReload() {return reload;}
        public void setReload(Reload reload) {this.reload = reload;}
        public Damage getDamage() {return damage;}
        public void setDamage(Damage damage) {this.damage = damage;}
        public Firearm_action getFirearm_action() {return firearm_action;}
        public void setFirearm_action(Firearm_action firearm_action) {this.firearm_action = firearm_action;}
        public Scope getScope() {return scope;}
        public void setScope(Scope scope) {this.scope = scope;}

        // Inner class to represent Info in the YAML structure
        public static class Info {
            private WeaponItem weapon_item;
            private Weapon_info_display weapon_info_display;
            private String weapon_get_mechanics;
            private Cancel cancel;
            private Dual_Wield dual_wield;


            // Getter and Setter for WeaponItem
            public WeaponItem getWeapon_item() {
                return weapon_item;
            }

            public Weapon_info_display getWeapon_info_display() {
                return weapon_info_display;
            }

            public void setWeapon_item(WeaponItem weapon_item) {
                this.weapon_item = weapon_item;
            }

            public void setWeapon_info_display(Weapon_info_display weapon_info_display) {
                this.weapon_info_display = weapon_info_display;
            }

            public String getWeapon_get_mechanics() {
                return weapon_get_mechanics;
            }

            public void setWeapon_get_mechanics(String weapon_get_mechanics) {
                this.weapon_get_mechanics = weapon_get_mechanics;
            }

            public Cancel getCancel() {
                return cancel;
            }

            public void setCancel(Cancel cancel) {
                this.cancel = cancel;
            }

            public Dual_Wield getDual_wield() {
                return dual_wield;
            }

            public void setDual_wield(Dual_Wield dual_wield) {
                this.dual_wield = dual_wield;
            }

            // Inner class to represent WeaponItem in the YAML structure
            public static class WeaponItem {
                private String type;
                private String name;
                private List<String> lore;
                private boolean unbreakable;
                private boolean hide_flags;
                private boolean deny_use_in_crafting;
                private boolean secondary_fire_type;
		private boolean burst_mode;

                // Getter and Setter for type
                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<String> getLore() {
                    return lore;
                }

                public void setLore(List<String> lore) {
                    this.lore = lore;
                }

                public boolean isUnbreakable() {
                    return unbreakable;
                }

                public void setUnbreakable(boolean unbreakable) {
                    this.unbreakable = unbreakable;
                }

                public boolean isHide_flags() {
                    return hide_flags;
                }

                public void setHide_flags(boolean hide_flags) {
                    this.hide_flags = hide_flags;
                }

                public boolean isDeny_use_in_crafting() {
                    return deny_use_in_crafting;
                }

                public void setDeny_use_in_crafting(boolean deny_use_in_crafting) {
                    this.deny_use_in_crafting = deny_use_in_crafting;
                }

                public void setSecondary_fire_type(boolean secondary_fire_type) {
                    this.secondary_fire_type = secondary_fire_type;
                }

                public boolean isSecondary_fire_type() {
                    return secondary_fire_type;
                }

		public void setBurst_mode(boolean burst_mode){
		    this.burst_mode = burst_mode;
		}
		public boolean isBurst_mode(){
		    return burst_mode;
		}


            }

            public static class Weapon_info_display {
                private Action_bar action_bar;

                public Action_bar getAction_bar() {
                    return action_bar;
                }

                public void setAction_bar(Action_bar action_bar) {
                    this.action_bar = action_bar;
                }

                public static class Action_bar {
                    private String message;

                    public String getMessage() {
                        return message;
                    }

                    public void setMessage(String message) {
                        this.message = message;
                    }
                }


            }

            public static class Cancel {
                private boolean drop_item;
                private boolean arm_swing_animation;
                private boolean break_blocks;

                public boolean isDrop_item() {
                    return drop_item;
                }

                public void setDrop_item(boolean drop_item) {
                    this.drop_item = drop_item;
                }

                public boolean isArm_swing_animation() {
                    return arm_swing_animation;
                }

                public void setArm_swing_animation(boolean arm_swing_animation) {
                    this.arm_swing_animation = arm_swing_animation;
                }

                public boolean isBreak_blocks() {
                    return break_blocks;
                }
                public void setBreak_blocks(boolean break_blocks) {
                    this.break_blocks = break_blocks;
                }
            }

            public static class Dual_Wield {
                private boolean whitelist;
                private List<String> weapons;
                private boolean unbreakable;
                private boolean hide_flags;
                private boolean deny_use_in_crafting;

                public boolean isWhitelist() {
                    return whitelist;
                }
                public void setWhitelist(boolean whitelist) {
                    this.whitelist = whitelist;
                }
                public List<String> getWeapons() {
                    return weapons;
                }
                public void setWeapons(List<String> weapons) {
                    this.weapons = weapons;
                }
                public boolean isUnbreakable() {
                    return unbreakable;
                }
                public void setUnbreakable(boolean unbreakable) {
                    this.unbreakable = unbreakable;
                }
                public boolean isHide_flags() {
                    return hide_flags;
                }
                public void setHide_flags(boolean hide_flags) {
                    this.hide_flags = hide_flags;
                }
                public boolean isDeny_use_in_crafting() {
                    return deny_use_in_crafting;
                }
                public void setDeny_use_in_crafting(boolean deny_use_in_crafting) {
                    this.deny_use_in_crafting = deny_use_in_crafting;
                }

            }

        }

        public static class Skin {

            //@JsonProperty("default")
            private Integer default_skin;
            private String scope;
            private String sprint;
            private String blue;
            private String red;

            public Integer getDefault_skin() {
                return default_skin;
            }

            public void setDefault_skin(Integer default_skin) {
                this.default_skin = default_skin;
            }

            public String getScope() {
                return scope;
            }

            public void setScope(String scope) {
                this.scope = scope;
            }

            public String getSprint() {
                return sprint;
            }

            public void setSprint(String sprint) {
                this.sprint = sprint;
            }

            public String getBlue() {
                return blue;
            }
            public void setBlue(String blue) {
                this.blue = blue;
            }
            public String getRed() {
                return red;
            }
            public void setRed(String red) {
                this.red = red;
            }


        }

        public static class Shoot {

            private Trigger trigger;
            private int projectile_speed;
            private int fully_automatic_shots_per_second;
            private int projectiles_per_shot;
            private int delay_between_shots;
	    private int shots_per_burst;
	    private int burst_fire_rate;
	    private int burst_restart_delay;
            private Spread spread;
            private List<String> mechanics;
	    

            public int getFully_automatic_shots_per_second() {
                return fully_automatic_shots_per_second;
            }

            public void setFully_automatic_shots_per_second(int fully_automatic_shots_per_second) {
                this.fully_automatic_shots_per_second = fully_automatic_shots_per_second;
            }

            // Getters and Setters
            public Trigger getTrigger() {
                return trigger;
            }

            public void setTrigger(Trigger trigger) {
                this.trigger = trigger;
            }

            public int getProjectile_speed() {return projectile_speed;}
            public void setProjectile_speed(int projectile_speed) {
                this.projectile_speed = projectile_speed;
            }
            public int getProjectiles_per_shot() {return projectiles_per_shot;}
            public void setProjectiles_per_shot(int projectiles_per_shot) {
                this.projectiles_per_shot = projectiles_per_shot;
            }
            public int getDelay_between_shots() {return delay_between_shots;}
            public void setDelay_between_shots(int delay_between_shots) {
                this.delay_between_shots = delay_between_shots;
            }

	    public List<String> getMechanics() {
                return mechanics;
            }
	    
	    public void setMechanics(List<String> mechanics) {
                this.mechanics = mechanics;
            }

	    public void setShots_per_burst(int shots_per_burst){
		this.shots_per_burst = shots_per_burst;
	    }

	    public int getShots_per_burst(){
		return shots_per_burst;
	    }

	    public void setBurst_fire_rate(int burst_fire_rate){
		this.burst_fire_rate = burst_fire_rate;
	    }
	    
	    public int getBurst_fire_rate(){
		return burst_fire_rate;
	    }
	     public void setBurst_restart_delay(int burst_restart_delay){
		 this.burst_restart_delay = burst_restart_delay;
	    }

	    public int getBurst_restart_delay(){
		return burst_restart_delay;
	    }


            public Spread getSpread() {
                return spread;
            }

            public void setSpread(Spread spread) {
                this.spread = spread;
            }
	   
	    
            // Inner classes
            public static class Trigger {
                private String main_hand;
                private String off_hand;
                private Circumstance circumstance;
                private boolean steady_with_sneak;

                // Getters and Setters
                public String getMain_hand() {
                    return main_hand;
                }
                public void setMain_hand(String main_hand) {
                    this.main_hand = main_hand;
                }
                public String getOff_hand() {
                    return off_hand;
                }
                public void setOff_hand(String off_hand) {
                    this.off_hand = off_hand;
                }


                public Circumstance getCircumstance() {
                    return circumstance;
                }

                public void setCircumstance(Circumstance circumstance) {
                    this.circumstance = circumstance;
                }

                public boolean isSteady_with_sneak() {
                    return steady_with_sneak;
                }
                public void setSteady_with_sneak(boolean steady_with_sneak) {
                    this.steady_with_sneak = steady_with_sneak;
                }

                public static class Circumstance {
                    private String swimming;
                    private String dual_wielding;
                    private String sprinting;

                    // Getters and Setters
                    public String getSwimming() {
                        return swimming;
                    }

                    public void setSwimming(String swimming) {
                        this.swimming = swimming;
                    }

                    public String getDual_wielding() {
                        return dual_wielding;
                    }
                    public void setDual_wielding(String dual_wielding) {
                        this.dual_wielding = dual_wielding;
                    }

                    public String getSprinting() {
                        return sprinting;
                    }

                    public void setSprinting(String sprinting) {
                        this.sprinting = sprinting;
                    }
                }
            }

            public static class Spread {
                private Spread_image spread_image;
                private double base_spread;
                private Modify_Spread_When modify_spread_when;


                public Spread_image getSpread_image() {
                    return spread_image;
                }
                public void setSpread_image(Spread_image spread_image) {
                    this.spread_image = spread_image;
                }

                public double getBase_spread() {
                    return base_spread;
                }
                public void setBase_spread(double base_spread) {
                    this.base_spread = base_spread;
                }

                public Modify_Spread_When getModify_spread_when() {
                    return modify_spread_when;
                }

                public void setModify_spread_when(Modify_Spread_When modify_spread_when) {
                    this.modify_spread_when = modify_spread_when;
                }

                public static class Spread_image {
                    private String name;
                    private int field_of_view_width;
                    private int field_of_view_height;

                    // Getters and Setters
                    public String getName() {
                        return name;
                    }
                    public void setName(String name) {
                        this.name = name;
                    }

                    public int getField_of_view_width() {
                        return field_of_view_width;
                    }
                    public void setField_of_view_width(int field_of_view_width) {
                        this.field_of_view_width = field_of_view_width;
                    }
                    public int getField_of_view_height() {
                        return field_of_view_height;
                    }
                    public void setField_of_view_height(int field_of_view_height) {
                        this.field_of_view_height = field_of_view_height;
                    }
                }

                public static class Modify_Spread_When {
                    private String in_midair;
                    private String sneaking;
                    private String zooming;
                    public String getIn_midair() {
                        return in_midair;
                    }
                    public void setIn_midair(String in_midair) {
                        this.in_midair = in_midair;
                    }
                    public String getSneaking() {
                        return sneaking;
                    }
                    public void setSneaking(String sneaking) {
                        this.sneaking = sneaking;
                    }

                    public String getZooming() {
                        return zooming;
                    }
                    public void setZooming(String zooming) {
                        this.zooming = zooming;
                    }
                }

            }
        }

        public static class Reload {

            private Ammo ammo;
            private Trigger trigger;
            private int magazine_size;
            private int ammo_per_reload;
            private int reload_duration;
            private List<String> start_mechanics;
            private List<String> finish_mechanics;

            // Getters and Setters
            public Ammo getAmmo() {
                return ammo;
            }

            public void setAmmo(Ammo ammo) {
                this.ammo = ammo;
            }

            public Trigger getTrigger() {
                return trigger;
            }

            public void setTrigger(Trigger trigger) {
                this.trigger = trigger;
            }

            public int getMagazine_size() {
                return magazine_size;
            }
            public void setMagazine_size(int magazine_size) {
                this.magazine_size = magazine_size;
            }
            public int getAmmo_per_reload() {
                return ammo_per_reload;
            }
            public void setAmmo_per_reload(int ammo_per_reload) {
                this.ammo_per_reload = ammo_per_reload;
            }
            public int getReload_duration() {
                return reload_duration;
            }
            public void setReload_duration(int reload_duration) {
                this.reload_duration = reload_duration;
            }
            public List<String> getStart_mechanics() {
                return start_mechanics;
            }
            public void setStart_mechanics(List<String> start_mechanics) {
                this.start_mechanics = start_mechanics;
            }
            public List<String> getFinish_mechanics() {
                return finish_mechanics;
            }
            public void setFinish_mechanics(List<String> finish_mechanics) {
                this.finish_mechanics = finish_mechanics;
            }


            // Inner classes
            public static class Ammo {
                private List<String> out_of_ammo_mechanics;
                private List<String> ammos;

                // Getters and Setters
                public List<String> getOutOf_ammo_mechanics() {
                    return out_of_ammo_mechanics;
                }

                public void setOut_of_ammo_mechanics(List<String> out_of_ammo_mechanics) {
                    this.out_of_ammo_mechanics = out_of_ammo_mechanics;
                }

                public List<String> getAmmos() {
                    return ammos;
                }

                public void setAmmos(List<String> ammos) {
                    this.ammos = ammos;
                }
            }

            public static class Trigger {
                private String main_hand;
                private String off_hand;

                // Getters and Setters
                public String getMain_hand() {
                    return main_hand;
                }
                public void setMain_hand(String main_hand) {
                    this.main_hand = main_hand;
                }
                public String getOff_hand() {
                    return off_hand;
                }
                public void setOff_hand(String off_hand) {
                    this.off_hand = off_hand;
                }
            }
        }

        public static class Damage {

            private double base_damage;
            private boolean flaming;
            private double armor_damage;
            private List<String> dropoff;
            private List<String> mechanics;
            private Head head;

            // Getters and Setters
            public double getBase_damage() {
                return base_damage;
            }

            public void setBase_damage(double base_damage) {
                this.base_damage = base_damage;
            }

            public boolean getFlaming() {
                return flaming;
            }
            public void setFlaming(boolean flaming) {
                this.flaming = flaming;
            }

            public double getArmor_damage() {
                return armor_damage;
            }

            public void setArmor_damage(double armor_damage) {
                this.armor_damage = armor_damage;
            }

            public List<String> getDropoff() {
                return dropoff;
            }

            public void setDropoff(List<String> dropoff) {
                this.dropoff = dropoff;
            }

            public List<String> getMechanics() {
                return mechanics;
            }

            public void setMechanics(List<String> mechanics) {
                this.mechanics = mechanics;
            }

            public Head getHead() {
                return head;
            }
            public void setHead(Head head) {
                this.head = head;
            }
            public static class Head {
                private double bonus_damage;
                private List<String> mechanics;

                public double getBonus_damage() {
                    return bonus_damage;
                }
                public void setBonus_damage(double bonus_damage) {
                    this.bonus_damage = bonus_damage;
                }
                public List<String> getMechanics() {
                    return mechanics;
                }
                public void setMechanics(List<String> mechanics) {
                    this.mechanics = mechanics;
                }

            }
        }

        public static class Scope {
            private Trigger trigger;
            private boolean night_vision;
            private double zoom_amount;
            private boolean unscope_after_shot;
            private List<String> mechanics;
            private Zoom_Off zoom_off;
            private Zoom_Stacking zoom_stacking;

            public double getZoom_amount() {
                return zoom_amount;
            }
            public void setZoom_amount(double zoom_amount) {
                this.zoom_amount = zoom_amount;
            }
            public boolean isUnscope_after_shot() {
                return unscope_after_shot;
            }
            public void setUnscope_after_shot(boolean unscope_after_shot) {
                this.unscope_after_shot = unscope_after_shot;
            }
            public List<String> getMechanics() {
                return mechanics;
            }
            public void setMechanics(List<String> mechanics) {
                this.mechanics = mechanics;
            }
            public Trigger getTrigger() {
                return trigger;
            }
            public void setTrigger(Trigger trigger) {
                this.trigger = trigger;
            }
            public boolean isNight_vision() {
                return night_vision;
            }
            public void setNight_vision(boolean night_vision) {
                this.night_vision = night_vision;
            }
            public Zoom_Off getZoom_off() {
                return zoom_off;
            }
            public void setZoom_off(Zoom_Off zoom_off) {
                this.zoom_off = zoom_off;
            }

            public Zoom_Stacking getZoom_stacking() {
                return zoom_stacking;
            }

            public void setZoom_stacking(Zoom_Stacking zoom_stacking) {
                this.zoom_stacking = zoom_stacking;
            }

            public static class Zoom_Stacking {
                private List<Integer> stacks;
                private List<String> mechanics;

                public List<Integer> getStacks() {
                    return stacks;
                }
                public void setStacks(List<Integer> stacks) {
                    this.stacks = stacks;
                }
                public List<String> getMechanics() {
                    return mechanics;
                }
                public void setMechanics(List<String> mechanics) {
                    this.mechanics = mechanics;
                }
            }

            public static class Zoom_Off {
                private List<String> mechanics;

                public List<String> getMechanics() {
                    return mechanics;
                }
                public void setMechanics(List<String> mechanics) {
                    this.mechanics = mechanics;
                }

            }

            public static class Trigger {
                private String main_hand;
                private String off_hand;

                public String getMain_hand() {
                    return main_hand;
                }
                public void setMain_hand(String main_hand) {
                    this.main_hand = main_hand;

                }
                public String getOff_hand() {
                    return off_hand;
                }
                public void setOff_hand(String off_hand) {
                    this.off_hand = off_hand;
                }
            }
        }

        public static class Firearm_action {

            private String type;
            private Action open;
            private Action close;

            // Getters and Setters
            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public Action getOpen() {
                return open;
            }

            public void setOpen(Action open) {
                this.open = open;
            }

            public Action getClose() {
                return close;
            }

            public void setClose(Action close) {
                this.close = close;
            }

            // Inner class representing the open/close actions
            public static class Action {
                private int time;
                private List<String> mechanics;

                // Getters and Setters
                public int getTime() {
                    return time;
                }

                public void setTime(int time) {
                    this.time = time;
                }

                public List<String> getMechanics() {
                    return mechanics;
                }

                public void setMechanics(List<String> mechanics) {
                    this.mechanics = mechanics;
                }
            }
        }



    }
}

