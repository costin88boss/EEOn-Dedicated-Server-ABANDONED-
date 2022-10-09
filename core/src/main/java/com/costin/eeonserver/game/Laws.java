package com.costin.eeonserver.game;

public class Laws {

    public static final String clientVersion = "0.0.1";
    public static final int tps = 60;
    public static final double physicsMSPerTick = 10;
    public static final double baseDrag = Math.pow(.9981, physicsMSPerTick) * 1.00016093f;
    public static final double noModDrag = Math.pow(.9900, physicsMSPerTick) * 1.00016093;
    public static final float playerForce = 50;
    public static final float jumpHeight = 26f;
    public static final float gravity = 2f;



    public static String  site= "https://everybodyedits.com/";
    public static String  url_blog= "https://blog.everybodyedits.com";
    public static String  url_goldmember_about_page= site + "gold";
    public static String  url_terms_page= site + "terms";
    public static String  url_help_page= site + "help";
    public static String  url_faq= site + "faq";
    public static String  url_forums= "https://forums.everybodyedits.com/";
    public static String  url_merch= "https://everybodyedits.wordpress.com/2018/08/01/everybody-edits-t-shirts/";
    public static String  url_patreon= "https://patreon.com/everybodyedits/overview/";

    //Mp debug
    public static Boolean  use_debug_server= false;
    public static Boolean  run_in_development_mode= false;
    public static Boolean  show_disabled_shopitems= false;
    public static String  development_mode_autojoin_room= "PWGosha";

    // News
    public static String  debug_news= ""; // "solitude" "scarecrow" "farm" "helen";

    public static String  developer_server= "127.0.0.1:8184";

    //Profile debug
    public static Boolean  forceBeta= false;

    public static String  debug_profile= "";
    public static String  debug_crew_profile= "";

    public static Boolean  disableCookie= false;

    public static Boolean  show_debug_friendrequest= false;
    public static String  debug_friendrequest= "";

    public static Boolean  show_blacklist_invitation= false;
    public static String  debug_invitation= "";
    public static int  termsVersion= 2;

    //Specefic to physics.
    public static int  physics_ms_per_tick = 10;
    public static float physics_variable_multiplyer= 1;// 7.752f;

    public static float physics_base_drag= (float) Math.pow(.9981,physics_ms_per_tick) * 1.00016093f;
    public static float physics_ice_no_mod_drag= (float) Math.pow(.9993,physics_ms_per_tick) * 1.00016093f;
    public static float physics_ice_drag= (float) Math.pow(.9998,physics_ms_per_tick) * 1.00016093f;
    //Multiplyer when not applying force by userkeys
    public static float physics_no_modifier_drag= (float) Math.pow(.9900,physics_ms_per_tick) * 1.00016093f;
    public static float physics_water_drag= (float) Math.pow(.9950,physics_ms_per_tick) * 1.00016093f;
    public static float physics_mud_drag= (float) Math.pow(.9750,physics_ms_per_tick) * 1.00016093f;
    public static float physics_lava_drag= (float) (Math.pow(.9800,physics_ms_per_tick) * 1.00016093f);
    public static float physics_toxic_drag= (float) Math.pow(.9900,physics_ms_per_tick) * 1.00016093f;
    public static float physics_jump_height= 26;

    public static float physics_gravity= 2;
    public static float physics_boost= 16;
    public static float physics_water_buoyancy= -.5f;
    public static float physics_mud_buoyancy= .4f;
    public static float physics_lava_buoyancy= .2f;
    public static float physics_toxic_buoyancy= -.4f;

    public static int  physics_queue_length= 2;

    //Other
    public static float camera_lag= 1/16f;

    public static Boolean  isMobile= false;

    public static Boolean  enableDebugShadow= false;

    public static int  maxwidth= 640; //wtf
    public static int  minwidth= 640;
    public static int  width= 640;
    public static int  height= 500;
    public static int  maxFrameRate= 120;
    public static int  lobbyFrameRate= 50;

    // Lobby Effects
    public static Boolean  displayFog= false;
    public static Boolean  displayBanner= false;
    //public static String  lobbyEffect= FallingItemMode.NONE;

    // Effects
    public static int  effectReset= -1;
    public static int  effectJump= 0;
    public static int  effectFly= 1;
    public static int  effectRun= 2;
    public static int  effectProtection= 3;
    public static int  effectCurse= 4;
    public static int  effectZombie= 5;
    // Team = 6
    public static int  effectLowGravity= 7;
    public static int  effectFire= 8;
    public static int  effectMultijump= 9;
    public static int  effectGravity= 10;
    public static int  effectPoison= 11;

    // Particle Engine
    public static int  max_Particles= 45;

    // Colors
    public static int  guest_color= 0xff333333;
    public static int  default_color= 0xffeeeeee;
    public static int  default_color_dark= 0xffcccccc;
    public static int  friend_color= 0xff00ff00;
    public static int  friend_color_dark= 0xff00bb00;
    //public static int  mod_color= 0xffffbb00;
    public static int  admin_color= 0xffffb400;
    public static int  moderator_color= 0xffce9ff4;
    public static int  developer_color= 0xff3399ff;
    public static int  designer_color= 0xffff7733;
    public static int  campaign_curator_color= 0xff77ddff;
    public static int  composer_color= 0xffff6a5a;
    public static int  patron_color_1= 0xffffff99;
    //public static int  patron_color_2= 0xffaaffbf;





    public static int[] getVersion() {
        String[] version = clientVersion.split("\\.");
        return new int[]{Integer.parseInt(version[0]), Integer.parseInt(version[1]), Integer.parseInt(version[2])};
    }

}
