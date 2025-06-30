package org.example;

import java.util.List;

public class ProjectileClass {

    private Assault_Rifle assault_rifle;
    private Sniper_Rifle sniper_rifle;
    private Shotgun shotgun;
    private Pistol pistol;

    public Assault_Rifle getAssault_rifle() {
        return assault_rifle;
    }

    public Pistol getPistol() {
        return pistol;
    }
    public Shotgun getShotgun() {
        return shotgun;
    }

    public Sniper_Rifle getSniper_rifle() {
        return sniper_rifle;
    }

    public void setAssault_rifle(Assault_Rifle assault_rifle) {
        this.assault_rifle = assault_rifle;
    }

    public void setPistol(Pistol pistol) {
        this.pistol = pistol;
    }

    public void setSniper_rifle(Sniper_Rifle sniper_rifle) {
        this.sniper_rifle = sniper_rifle;
    }

    public void setShotgun(Shotgun shotgun) {
        this.shotgun = shotgun;
    }

    public static class Assault_Rifle {
        private Projectile projectile;


        public Projectile getProjectile() {
            return projectile;
        }

        public void setProjectile(Projectile projectile) {
            this.projectile = projectile;
        }



        // Getters and setters for the fields

        public static class Projectile {
            private ProjectileSettings projectile_settings;
            private Through through;

            public ProjectileSettings getProjectile_settings() {
                return projectile_settings;
            }

            public void setProjectile_settings(ProjectileSettings projectile_settings) {
                this.projectile_settings = projectile_settings;
            }
            public Through getThrough() {
                return through;
            }

            public void setThrough(Through through) {
                this.through = through;
            }

            public static class ProjectileSettings {
                private String type;
                private Minimum minimum;
                private double gravity;
                private Drag drag;

                public void setType(String type) {
                    this.type = type;
                }

                public String getType() {
                    return type;
                }

                public double getGravity() {
                    return gravity;
                }

                public Drag getDrag() {
                    return drag;
                }

                public Minimum getMinimum() {
                    return minimum;
                }

                public void setDrag(Drag drag) {
                    this.drag = drag;
                }

                public void setGravity(double gravity) {
                    this.gravity = gravity;
                }

                public void setMinimum(Minimum minimum) {
                    this.minimum = minimum;
                }


                public static class Minimum {
                    private int speed;

                    public int getSpeed() {
                        return speed;
                    }

                    public void setSpeed(int speed) {
                        this.speed = speed;
                    }
                    // Getter and setter for speed
                }

                public static class Drag {
                    private double base;
                    private double in_water;
                    private double when_raining_or_snowing;

                    public double getBase() {
                        return base;
                    }

                    public double getIn_water() {
                        return in_water;
                    }

                    public double getWhen_raining_or_snowing() {
                        return when_raining_or_snowing;
                    }

                    public void setBase(double base) {
                        this.base = base;
                    }

                    public void setIn_water(double in_water) {
                        this.in_water = in_water;
                    }

                    public void setWhen_raining_or_snowing(double when_raining_or_snowing) {
                        this.when_raining_or_snowing = when_raining_or_snowing;
                    }

                }

            }
            public static class Through {
                private int maximum_through_amount;
                private Blocks blocks;
                private Entities entities;

                public Blocks getBlocks() {
                    return blocks;
                }

                public Entities getEntities() {
                    return entities;
                }

                public int getMaximum_through_amount() {
                    return maximum_through_amount;
                }

                public void setBlocks(Blocks blocks) {
                    this.blocks = blocks;
                }

                public void setEntities(Entities entities) {
                    this.entities = entities;
                }

                public void setMaximum_through_amount(int maximum_through_amount) {
                    this.maximum_through_amount = maximum_through_amount;
                }
        }




            public static class Blocks {
                private double default_speed_multiplier;
                private boolean whitelist;
                private List<String> list;

                public boolean isWhitelist() {
                    return whitelist;
                }

                public double getDefault_speed_multiplier() {
                    return default_speed_multiplier;
                }

                public List<String> getList() {
                    return list;
                }

                public void setDefault_speed_multiplier(double default_speed_multiplier) {
                    this.default_speed_multiplier = default_speed_multiplier;
                }

                public void setList(List<String> list) {
                    this.list = list;
                }

                public void setWhitelist(boolean whitelist) {
                    this.whitelist = whitelist;
                }

                // Getters and setters for the fields
            }

            public static class Entities {
                private boolean allow_any;
                private double default_speed_multiplier;

                public void setDefault_speed_multiplier(double default_speed_multiplier) {
                    this.default_speed_multiplier = default_speed_multiplier;
                }

                public double getDefault_speed_multiplier() {
                    return default_speed_multiplier;
                }

                public boolean isAllow_any() {
                    return allow_any;
                }

                public void setAllow_any(boolean allow_any) {
                    this.allow_any = allow_any;
                }
                // Getters and setters for the fields
            }
        }


    }
    public static class Sniper_Rifle {
        private Projectile projectile;

        public Projectile getProjectile() {
            return projectile;
        }

        public void setProjectile(Projectile projectile) {
            this.projectile = projectile;
        }



        // Getters and setters for the fields

        public static class Projectile {
            private ProjectileSettings projectile_settings;
            private Through through;

            public ProjectileSettings getProjectile_settings() {
                return projectile_settings;
            }

            public void setProjectile_settings(ProjectileSettings projectile_settings) {
                this.projectile_settings = projectile_settings;
            }
            public Through getThrough() {
                return through;
            }

            public void setThrough(Through through) {
                this.through = through;
            }


            public static class ProjectileSettings {
                private String type;
                private Minimum minimum;
                private double gravity;
                private Drag drag;

                public void setType(String type) {
                    this.type = type;
                }

                public String getType() {
                    return type;
                }

                public double getGravity() {
                    return gravity;
                }

                public Drag getDrag() {
                    return drag;
                }

                public Minimum getMinimum() {
                    return minimum;
                }

                public void setDrag(Drag drag) {
                    this.drag = drag;
                }

                public void setGravity(double gravity) {
                    this.gravity = gravity;
                }

                public void setMinimum(Minimum minimum) {
                    this.minimum = minimum;
                }



                public static class Minimum {
                    private int speed;

                    public int getSpeed() {
                        return speed;
                    }

                    public void setSpeed(int speed) {
                        this.speed = speed;
                    }
                    // Getter and setter for speed
                }

                public static class Drag {
                    private double base;
                    private double in_water;
                    private double when_raining_or_snowing;

                    public double getBase() {
                        return base;
                    }

                    public double getIn_water() {
                        return in_water;
                    }

                    public double getWhen_raining_or_snowing() {
                        return when_raining_or_snowing;
                    }

                    public void setBase(double base) {
                        this.base = base;
                    }

                    public void setIn_water(double in_water) {
                        this.in_water = in_water;
                    }

                    public void setWhen_raining_or_snowing(double when_raining_or_snowing) {
                        this.when_raining_or_snowing = when_raining_or_snowing;
                    }

                }

            }
            public static class Through {
                private int maximum_through_amount;
                private Blocks blocks;
                private Entities entities;

                public Blocks getBlocks() {
                    return blocks;
                }

                public Entities getEntities() {
                    return entities;
                }

                public int getMaximum_through_amount() {
                    return maximum_through_amount;
                }

                public void setBlocks(Blocks blocks) {
                    this.blocks = blocks;
                }

                public void setEntities(Entities entities) {
                    this.entities = entities;
                }

                public void setMaximum_through_amount(int maximum_through_amount) {
                    this.maximum_through_amount = maximum_through_amount;
                }
        }



            public static class Blocks {
                private double default_speed_multiplier;
                private boolean whitelist;
                private List<String> list;

                public boolean isWhitelist() {
                    return whitelist;
                }

                public double getDefault_speed_multiplier() {
                    return default_speed_multiplier;
                }

                public List<String> getList() {
                    return list;
                }

                public void setDefault_speed_multiplier(double default_speed_multiplier) {
                    this.default_speed_multiplier = default_speed_multiplier;
                }

                public void setList(List<String> list) {
                    this.list = list;
                }

                public void setWhitelist(boolean whitelist) {
                    this.whitelist = whitelist;
                }

                // Getters and setters for the fields
            }

            public static class Entities {
                private boolean allow_any;
                private double default_speed_multiplier;

                public void setDefault_speed_multiplier(double default_speed_multiplier) {
                    this.default_speed_multiplier = default_speed_multiplier;
                }

                public double getDefault_speed_multiplier() {
                    return default_speed_multiplier;
                }

                public boolean isAllow_any() {
                    return allow_any;
                }

                public void setAllow_any(boolean allow_any) {
                    this.allow_any = allow_any;
                }
                // Getters and setters for the fields
            }
        }


    }
    public static class Shotgun {
        private Projectile projectile;
        private Through through;

        public Projectile getProjectile() {
            return projectile;
        }

        public void setProjectile(Projectile projectile) {
            this.projectile = projectile;
        }

        public Through getThrough() {
            return through;
        }

        public void setThrough(Through through) {
            this.through = through;
        }

        // Getters and setters for the fields

        public static class Projectile {
            private ProjectileSettings projectile_settings;

            public ProjectileSettings getProjectile_settings() {
                return projectile_settings;
            }

            public void setProjectile_settings(ProjectileSettings projectile_settings) {
                this.projectile_settings = projectile_settings;
            }

            public static class ProjectileSettings {
                private String type;
                private Minimum minimum;
                private double gravity;
                private Drag drag;

                public void setType(String type) {
                    this.type = type;
                }

                public String getType() {
                    return type;
                }

                public double getGravity() {
                    return gravity;
                }

                public Drag getDrag() {
                    return drag;
                }

                public Minimum getMinimum() {
                    return minimum;
                }

                public void setDrag(Drag drag) {
                    this.drag = drag;
                }

                public void setGravity(double gravity) {
                    this.gravity = gravity;
                }

                public void setMinimum(Minimum minimum) {
                    this.minimum = minimum;
                }


                public static class Minimum {
                    private int speed;

                    public int getSpeed() {
                        return speed;
                    }

                    public void setSpeed(int speed) {
                        this.speed = speed;
                    }
                    // Getter and setter for speed
                }

                public static class Drag {
                    private double base;
                    private double in_water;
                    private double when_raining_or_snowing;

                    public double getBase() {
                        return base;
                    }

                    public double getIn_water() {
                        return in_water;
                    }

                    public double getWhen_raining_or_snowing() {
                        return when_raining_or_snowing;
                    }

                    public void setBase(double base) {
                        this.base = base;
                    }

                    public void setIn_water(double in_water) {
                        this.in_water = in_water;
                    }

                    public void setWhen_raining_or_snowing(double when_raining_or_snowing) {
                        this.when_raining_or_snowing = when_raining_or_snowing;
                    }

                }

            }
        }


        public static class Through {
            private int maximum_through_amount;
            private Blocks blocks;
            private Entities entities;

            public Blocks getBlocks() {
                return blocks;
            }

            public Entities getEntities() {
                return entities;
            }

            public int getMaximum_through_amount() {
                return maximum_through_amount;
            }

            public void setBlocks(Blocks blocks) {
                this.blocks = blocks;
            }

            public void setEntities(Entities entities) {
                this.entities = entities;
            }

            public void setMaximum_through_amount(int maximum_through_amount) {
                this.maximum_through_amount = maximum_through_amount;
            }


            public static class Blocks {
                private double default_speed_multiplier;
                private boolean whitelist;
                private List<String> list;

                public boolean isWhitelist() {
                    return whitelist;
                }

                public double getDefault_speed_multiplier() {
                    return default_speed_multiplier;
                }

                public List<String> getList() {
                    return list;
                }

                public void setDefault_speed_multiplier(double default_speed_multiplier) {
                    this.default_speed_multiplier = default_speed_multiplier;
                }

                public void setList(List<String> list) {
                    this.list = list;
                }

                public void setWhitelist(boolean whitelist) {
                    this.whitelist = whitelist;
                }

                // Getters and setters for the fields
            }

            public static class Entities {
                private boolean allow_any;
                private double default_speed_multiplier;

                public void setDefault_speed_multiplier(double default_speed_multiplier) {
                    this.default_speed_multiplier = default_speed_multiplier;
                }

                public double getDefault_speed_multiplier() {
                    return default_speed_multiplier;
                }

                public boolean isAllow_any() {
                    return allow_any;
                }

                public void setAllow_any(boolean allow_any) {
                    this.allow_any = allow_any;
                }
                // Getters and setters for the fields
            }
        }


    }
    public static class Pistol {
        private Projectile projectile;
        private Through through;

        public Projectile getProjectile() {
            return projectile;
        }

        public void setProjectile(Projectile projectile) {
            this.projectile = projectile;
        }

        public Through getThrough() {
            return through;
        }

        public void setThrough(Through through) {
            this.through = through;
        }

        // Getters and setters for the fields

        public static class Projectile {
            private ProjectileSettings projectile_settings;

            public ProjectileSettings getProjectile_settings() {
                return projectile_settings;
            }

            public void setProjectile_settings(ProjectileSettings projectile_settings) {
                this.projectile_settings = projectile_settings;
            }

            public static class ProjectileSettings {
                private String type;
                private Minimum minimum;
                private double gravity;
                private Drag drag;

                public void setType(String type) {
                    this.type = type;
                }

                public String getType() {
                    return type;
                }

                public double getGravity() {
                    return gravity;
                }

                public Drag getDrag() {
                    return drag;
                }

                public Minimum getMinimum() {
                    return minimum;
                }

                public void setDrag(Drag drag) {
                    this.drag = drag;
                }

                public void setGravity(double gravity) {
                    this.gravity = gravity;
                }

                public void setMinimum(Minimum minimum) {
                    this.minimum = minimum;
                }


                public static class Minimum {
                    private int speed;

                    public int getSpeed() {
                        return speed;
                    }

                    public void setSpeed(int speed) {
                        this.speed = speed;
                    }
                    // Getter and setter for speed
                }

                public static class Drag {
                    private double base;
                    private double in_water;
                    private double when_raining_or_snowing;

                    public double getBase() {
                        return base;
                    }

                    public double getIn_water() {
                        return in_water;
                    }

                    public double getWhen_raining_or_snowing() {
                        return when_raining_or_snowing;
                    }

                    public void setBase(double base) {
                        this.base = base;
                    }

                    public void setIn_water(double in_water) {
                        this.in_water = in_water;
                    }

                    public void setWhen_raining_or_snowing(double when_raining_or_snowing) {
                        this.when_raining_or_snowing = when_raining_or_snowing;
                    }

                }

            }
        }


        public static class Through {
            private int maximum_through_amount;
            private Blocks blocks;
            private Entities entities;

            public Blocks getBlocks() {
                return blocks;
            }

            public Entities getEntities() {
                return entities;
            }

            public int getMaximum_through_amount() {
                return maximum_through_amount;
            }

            public void setBlocks(Blocks blocks) {
                this.blocks = blocks;
            }

            public void setEntities(Entities entities) {
                this.entities = entities;
            }

            public void setMaximum_through_amount(int maximum_through_amount) {
                this.maximum_through_amount = maximum_through_amount;
            }


            public static class Blocks {
                private double default_speed_multiplier;
                private boolean whitelist;
                private List<String> list;

                public boolean isWhitelist() {
                    return whitelist;
                }

                public double getDefault_speed_multiplier() {
                    return default_speed_multiplier;
                }

                public List<String> getList() {
                    return list;
                }

                public void setDefault_speed_multiplier(double default_speed_multiplier) {
                    this.default_speed_multiplier = default_speed_multiplier;
                }

                public void setList(List<String> list) {
                    this.list = list;
                }

                public void setWhitelist(boolean whitelist) {
                    this.whitelist = whitelist;
                }

                // Getters and setters for the fields
            }

            public static class Entities {
                private boolean allow_any;
                private double default_speed_multiplier;

                public void setDefault_speed_multiplier(double default_speed_multiplier) {
                    this.default_speed_multiplier = default_speed_multiplier;
                }

                public double getDefault_speed_multiplier() {
                    return default_speed_multiplier;
                }

                public boolean isAllow_any() {
                    return allow_any;
                }

                public void setAllow_any(boolean allow_any) {
                    this.allow_any = allow_any;
                }
                // Getters and setters for the fields
            }
        }


    }


}
