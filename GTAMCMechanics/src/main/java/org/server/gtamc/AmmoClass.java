package org.server.gtamc;

public class AmmoClass {

    private Assaultrifle_Ammo assaultrifle_ammo;
    private Sniper_Ammo sniper_ammo;
    private Shotgun_Ammo shotgun_ammo;
    private Pistol_Ammo pistol_ammo;

    public Assaultrifle_Ammo getAssaultrifle_ammo() {
        return assaultrifle_ammo;
    }
    public void setAssaultrifle_ammo(Assaultrifle_Ammo assaultrifle_ammo) {this.assaultrifle_ammo = assaultrifle_ammo;}
    public Pistol_Ammo getPistol_ammo() {
        return pistol_ammo;
    }
    public Shotgun_Ammo getShotgun_ammo() {
        return shotgun_ammo;
    }
    public Sniper_Ammo getSniper_ammo() {
        return sniper_ammo;
    }
    public void setPistol_ammo(Pistol_Ammo pistol_ammo) {
        this.pistol_ammo = pistol_ammo;
    }
    public void setShotgun_ammo(Shotgun_Ammo shotgun_ammo) {
        this.shotgun_ammo = shotgun_ammo;
    }
    public void setSniper_ammo(Sniper_Ammo sniper_ammo) {
        this.sniper_ammo = sniper_ammo;
    }

    public static class Assaultrifle_Ammo {
        private Item_Ammo item_ammo;

        public Item_Ammo getItem_ammo() {
            return item_ammo;
        }
        public void setItem_ammo(Item_Ammo item_ammo) {
            this.item_ammo = item_ammo;
        }

        public static class Item_Ammo {
            public Bullet_Item bullet_item;

            public Bullet_Item getBullet_item() {
                return bullet_item;
            }
            public void setBullet_item(Bullet_Item bullet_item) {
                this.bullet_item = bullet_item;
            }

            public static class Bullet_Item {
                private String type;
                private String name;

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
            }
        }
    }

    public static class Sniper_Ammo {
        private Item_Ammo item_ammo;

        public Item_Ammo getItem_ammo() {
            return item_ammo;
        }
        public void setItem_ammo(Item_Ammo item_ammo) {
            this.item_ammo = item_ammo;
        }

        public static class Item_Ammo {
            public Bullet_Item bullet_item;
            public Bullet_Item getBullet_item() {
                return bullet_item;
            }
            public void setBullet_item(Bullet_Item bullet_item) {
                this.bullet_item = bullet_item;
            }


            public static class Bullet_Item {
                private String type;
                private String name;
                public String getType() {
                    return type;
                }
                public void setType(String type) {
                    this.type = type;
                }
                public String getName() {
                    return name;
                }

                public void setName(String name) {this.name = name;}
            }
        }
    }

    public static class Shotgun_Ammo {
        private Item_Ammo item_ammo;

        public Item_Ammo getItem_ammo() {
            return item_ammo;
        }
        public void setItem_ammo(Item_Ammo item_ammo) {
            this.item_ammo = item_ammo;
        }

        public static class Item_Ammo {
            public Bullet_Item bullet_item;
            public Bullet_Item getBullet_item() {
                return bullet_item;
            }
            public void setBullet_item(Bullet_Item bullet_item) {
                this.bullet_item = bullet_item;
            }


            public static class Bullet_Item {
                private String type;
                private String name;
                public String getType() {
                    return type;
                }
                public void setType(String type) {
                    this.type = type;
                }
                public String getName() {
                    return name;
                }

                public void setName(String name) {this.name = name;}
            }
        }
    }

    public static class Pistol_Ammo {
        private Item_Ammo item_ammo;

        public Item_Ammo getItem_ammo() {
            return item_ammo;
        }
        public void setItem_ammo(Item_Ammo item_ammo) {
            this.item_ammo = item_ammo;
        }

        public static class Item_Ammo {
            public Bullet_Item bullet_item;
            public Bullet_Item getBullet_item() {
                return bullet_item;
            }
            public void setBullet_item(Bullet_Item bullet_item) {
                this.bullet_item = bullet_item;
            }


            public static class Bullet_Item {
                private String type;
                private String name;
                public String getType() {
                    return type;
                }
                public void setType(String type) {
                    this.type = type;
                }
                public String getName() {
                    return name;
                }
                public void setName(String name) {this.name = name;}
            }
        }
    }
}
