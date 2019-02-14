/**
 * RobinhoodTest.java  v0.1  4 March 2018 3:25:35 PM
 *
 * Copyright © 2018 Daniel Kuan.  All rights reserved.
 */
package org.ikankechil.eod3.sources;

import static org.ikankechil.eod3.sources.Exchanges.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

import org.ikankechil.eod3.Frequencies;

/**
 * JUnit test for <code>Robinhood</code>.
 *
 *
 * @author Daniel Kuan
 * @version 0.1
 */
public class RobinhoodTest extends SourceTest {

  private static final String BASE                     = baseURL(RobinhoodTest.class);
  private static final String INTERVAL_DAY_SPAN_YEAR   = "/?interval=day&span=year";
  private static final String INTERVAL_WEEK_SPAN_5YEAR = "/?interval=week&span=5year";
  private static final String SUFFIX                   = "&bounds=regular";

  public RobinhoodTest() throws IOException {
    exchanges.put(NYSE, EMPTY);
    exchanges.put(NASDAQ, EMPTY);
    exchanges.put(AMEX, EMPTY);
    exchanges.put(ARCA, EMPTY);

    readOriginalLinesFromJsonFile();

    transformedLines.addAll(Arrays.asList("INTC,20180302,47.2000,49.0500,46.9600,48.9800,33310592",
                                          "INTC,20180301,49.5000,49.7150,47.4700,47.8400,36326639",
                                          "INTC,20180228,50.1800,50.3400,49.2800,49.2900,35541183",
                                          "INTC,20180227,49.3700,50.9000,49.3100,49.9100,43624077",
                                          "INTC,20180226,48.2000,49.1300,48.1000,49.1100,26992306",
                                          "INTC,20180223,46.3400,47.7900,46.3050,47.7300,26040911",
                                          "INTC,20180222,46.1400,46.5600,45.6200,45.8000,16946353",
                                          "INTC,20180221,46.6700,47.0600,45.9200,45.9400,27527487",
                                          "INTC,20180220,45.4600,46.7100,45.3900,46.3200,25511962",
                                          "INTC,20180216,45.8150,46.5900,45.5100,45.5600,21475207",
                                          "INTC,20180215,45.8800,45.9900,44.9500,45.9200,23536793",
                                          "INTC,20180214,44.0700,45.5100,44.0400,45.3800,19922010",
                                          "INTC,20180213,44.5900,44.7750,44.2550,44.4600,19517609",
                                          "INTC,20180212,44.1500,45.1600,44.0900,44.8300,28379521",
                                          "INTC,20180209,43.5000,44.2800,42.0400,43.9500,49301141",
                                          "INTC,20180208,45.2800,45.4200,42.7400,42.7500,43151330",
                                          "INTC,20180207,44.5800,46.3000,44.3400,45.2000,37508414",
                                          "INTC,20180206,44.0200,44.9300,43.0302,44.9100,58145381",
                                          "INTC,20180205,46.0377,46.9415,44.2001,44.2200,47869041",
                                          "INTC,20180202,47.0011,47.4680,45.7894,45.8390,28663029",
                                          "INTC,20180201,47.3736,48.1434,47.1303,47.3289,28840380",
                                          "INTC,20180131,48.8983,48.9181,47.2395,47.8156,41619914",
                                          "INTC,20180130,49.1466,49.7227,48.4116,48.4612,32814590",
                                          "INTC,20180129,49.2658,50.5073,49.0373,49.6432,46304040",
                                          "INTC,20180126,48.0540,49.8121,47.7957,49.7425,86916079",
                                          "INTC,20180125,45.5708,46.0575,44.8656,44.9947,36758746",
                                          "INTC,20180124,45.5907,45.8192,44.6570,45.2033,32464221",
                                          "INTC,20180123,45.5013,45.7794,45.3225,45.7496,22135103",
                                          "INTC,20180122,44.4981,45.4814,44.4882,45.4417,27706762",
                                          "INTC,20180119,44.3193,44.5875,43.9518,44.5180,25780217",
                                          "INTC,20180118,44.1008,44.4336,43.8724,44.1803,26826529",
                                          "INTC,20180117,43.2516,44.1505,43.2168,44.0909,32368612",
                                          "INTC,20180116,43.2565,43.4949,42.5960,42.8493,38367181",
                                          "INTC,20180112,43.1572,43.3062,42.7202,42.9486,29973553",
                                          "INTC,20180111,42.5116,43.2863,42.1639,43.1175,35371519",
                                          "INTC,20180110,43.0380,43.3062,42.1540,42.2136,45734951",
                                          "INTC,20180109,44.3988,44.5378,43.1969,43.3261,44282311",
                                          "INTC,20180108,43.9717,44.5378,43.6638,44.4385,33733769",
                                          "INTC,20180105,44.1306,44.8458,43.6042,44.4385,41824006",
                                          "INTC,20180104,43.2267,44.3491,42.4023,44.1306,89209119",
                                          "INTC,20180103,45.1636,45.8986,43.3559,44.9550,116478986",
                                          "INTC,20180102,46.0675,46.5840,45.8986,46.5343,23370791",
                                          "INTC,20171229,45.8986,46.1767,45.7794,45.8489,17394491",
                                          "INTC,20171228,46.0476,46.0476,45.6404,45.9085,9504372",
                                          "INTC,20171227,45.7993,46.0476,45.6900,45.7993,13359671",
                                          "INTC,20171226,45.9681,46.1569,45.6404,45.7695,15533328",
                                          "INTC,20171222,46.0178,46.7032,45.7099,46.3853,33913537",
                                          "INTC,20171221,47.2196,47.2693,46.2463,46.4449,42963451",
                                          "INTC,20171220,47.1501,47.3190,46.3456,47.2395,40934978",
                                          "INTC,20171219,45.9880,46.9415,45.5808,46.7230,41421116",
                                          "INTC,20171218,44.8458,46.0377,44.7365,45.9483,50369327",
                                          "INTC,20171215,43.1075,44.5378,42.7798,44.2597,47476952",
                                          "INTC,20171214,43.1373,43.2764,42.7251,42.9685,19644917",
                                          "INTC,20171213,43.3559,43.3658,42.9486,43.0480,21399460",
                                          "INTC,20171212,43.1771,43.3261,42.7897,43.0380,16557398",
                                          "INTC,20171211,42.9884,43.4850,42.9586,43.3658,20425900",
                                          "INTC,20171208,43.0579,43.2863,42.8195,43.0579,23154749",
                                          "INTC,20171207,43.1671,43.3062,42.4917,42.7897,32708441",
                                          "INTC,20171206,42.8493,43.4254,42.3825,43.1572,27710812",
                                          "INTC,20171205,44.2995,44.5974,42.9387,43.1473,30626850",
                                          "INTC,20171204,44.7166,44.9947,44.0313,44.1902,28000791",
                                          "INTC,20171201,44.4286,44.5378,43.2367,44.3789,26656272",
                                          "INTC,20171130,44.0611,44.8954,44.0313,44.5378,34145303",
                                          "INTC,20171129,44.5478,44.7862,43.4751,43.6538,27036870",
                                          "INTC,20171128,44.3491,44.5378,44.0213,44.4286,20194062",
                                          "INTC,20171127,44.1207,44.3094,43.9753,44.1902,18202115",
                                          "INTC,20171122,44.6372,44.6471,44.2349,44.3491,19538331",
                                          "INTC,20171121,44.4187,44.9153,44.4087,44.6372,21871027",
                                          "INTC,20171120,44.4286,44.8110,44.2001,44.3193,22420003",
                                          "INTC,20171117,45.1934,45.3126,44.3193,44.3293,63798040",
                                          "INTC,20171116,45.3225,45.7596,45.1537,45.3424,26439406",
                                          "INTC,20171115,45.0841,45.4963,44.8681,45.1537,17157204",
                                          "INTC,20171114,45.3920,45.6106,45.0146,45.5510,22201170",
                                          "INTC,20171113,44.9550,45.6304,44.9451,45.4417,18999002",
                                          "INTC,20171110,45.7298,45.7794,45.0742,45.2729,24095354",
                                          "INTC,20171109,45.7397,46.0774,45.3424,45.9880,25570358",
                                          "INTC,20171108,46.3058,46.3853,45.9681,46.3853,21565759",
                                          "INTC,20171107,46.3853,46.7727,46.0774,46.4648,24461371",
                                          "INTC,20171106,46.2860,46.4250,45.7794,46.3853,34034971",
                                          "INTC,20171103,46.6063,46.7050,45.0264,45.7571,39298110",
                                          "INTC,20171102,46.0237,46.6359,45.6317,46.5075,44602250",
                                          "INTC,20171101,45.3917,46.1224,45.0165,46.1224,47813724",
                                          "INTC,20171031,44.6314,45.2239,44.0883,44.9178,47000920",
                                          "INTC,20171030,43.9205,44.6215,43.3280,43.8119,48161518",
                                          "INTC,20171027,42.7553,44.4339,42.5578,43.8415,90028300",
                                          "INTC,20171026,40.3954,41.0520,40.1979,40.8298,38247395",
                                          "INTC,20171025,40.3756,40.5386,39.9807,40.2670,24031660",
                                          "INTC,20171024,40.4842,40.5336,40.2078,40.4349,19652417",
                                          "INTC,20171023,40.0399,40.5237,39.8869,40.3164,28646966",
                                          "INTC,20171020,39.8227,39.9412,39.5956,39.9214,18335672",
                                          "INTC,20171019,39.3783,39.8227,39.3290,39.5857,19222705",
                                          "INTC,20171018,39.2993,39.7832,39.1019,39.7437,21424554",
                                          "INTC,20171017,39.0624,39.3635,38.8748,39.2895,15532818",
                                          "INTC,20171016,39.2105,39.2895,38.9439,39.2598,12489135",
                                          "INTC,20171013,38.9439,39.3092,38.7859,39.1710,16829366",
                                          "INTC,20171012,38.8550,38.8945,38.4897,38.6970,18286944",
                                          "INTC,20171011,38.9834,39.1710,38.5686,38.8056,30754708",
                                          "INTC,20171010,39.4277,39.4475,38.8846,39.1512,29890017",
                                          "INTC,20171009,39.1809,39.3783,39.0229,39.3586,18494080",
                                          "INTC,20171006,39.1019,39.3882,38.9241,39.1315,18887536",
                                          "INTC,20171005,39.0031,39.1512,38.7168,39.0327,17710277",
                                          "INTC,20171004,38.8945,38.9044,38.3718,38.8451,28368824",
                                          "INTC,20171003,38.4600,39.2006,38.4600,38.8846,34002193",
                                          "INTC,20171002,37.6405,38.5983,37.6010,38.5489,37394514",
                                          "INTC,20170929,37.3640,37.6701,37.2258,37.6010,23217336",
                                          "INTC,20170928,36.8505,37.4035,36.8209,37.3541,21171441",
                                          "INTC,20170927,37.1468,37.2159,36.6333,37.0678,25981384",
                                          "INTC,20170926,36.7419,37.1665,36.5346,36.9987,29790369",
                                          "INTC,20170925,36.5839,36.7617,36.3865,36.6926,23180838",
                                          "INTC,20170922,36.4852,36.7518,36.4852,36.7123,21631754",
                                          "INTC,20170921,36.5247,36.8012,36.3865,36.7320,22954158",
                                          "INTC,20170920,36.7617,36.8209,36.1939,36.6037,23957476",
                                          "INTC,20170919,36.7320,36.8259,36.5543,36.7617,23850525",
                                          "INTC,20170918,36.5346,36.8604,36.3371,36.5346,19393834",
                                          "INTC,20170915,36.0902,36.6136,35.7644,36.5346,33596072",
                                          "INTC,20170914,35.7348,36.2334,35.7051,36.0211,18093946",
                                          "INTC,20170913,35.5471,35.9421,35.5175,35.8730,15509787",
                                          "INTC,20170912,35.4287,35.8829,35.2904,35.6360,19489892",
                                          "INTC,20170911,35.0436,35.5471,34.6980,35.3200,20037575",
                                          "INTC,20170908,34.7473,35.0929,34.6387,34.7473,14125018",
                                          "INTC,20170907,35.4287,35.4978,34.8856,35.0929,16297077",
                                          "INTC,20170906,34.7770,35.4879,34.6683,35.3102,28076438",
                                          "INTC,20170905,34.5795,34.8856,34.4906,34.5795,18772047",
                                          "INTC,20170901,34.7967,34.9448,34.6288,34.6486,12821972",
                                          "INTC,20170831,34.5005,34.7375,34.4314,34.6288,16366772",
                                          "INTC,20170830,34.3129,34.5202,34.1944,34.4511,18565007",
                                          "INTC,20170829,34.0759,34.3129,34.0216,34.2931,15843668",
                                          "INTC,20170828,34.3425,34.3622,34.1549,34.2141,20712876",
                                          "INTC,20170825,34.3820,34.4906,34.1450,34.2339,14726829",
                                          "INTC,20170824,34.2635,34.4511,34.1154,34.2734,14301892",
                                          "INTC,20170823,34.1055,34.3721,33.9475,34.2240,19648134",
                                          "INTC,20170822,34.5795,34.7473,34.1845,34.2141,26097791",
                                          "INTC,20170821,34.6486,34.8362,34.2635,34.4807,26493271",
                                          "INTC,20170818,34.8461,34.8658,34.5498,34.5696,16231408",
                                          "INTC,20170817,35.1522,35.2312,34.7276,34.7276,19524961",
                                          "INTC,20170816,35.5274,35.6163,35.1127,35.3595,22013991",
                                          "INTC,20170815,35.8434,35.8631,35.3645,35.5471,21706563",
                                          "INTC,20170814,35.6656,36.0112,35.6261,35.8829,18469448",
                                          "INTC,20170811,35.8039,35.9421,35.3398,35.4188,19275060",
                                          "INTC,20170810,35.9421,36.1001,35.6558,35.6854,22693316",
                                          "INTC,20170809,35.8236,36.1988,35.5669,36.1297,22796797",
                                          "INTC,20170808,35.9125,36.2383,35.8434,35.9520,22819871",
                                          "INTC,20170807,35.9322,36.0902,35.7644,35.9717,18169335",
                                          "INTC,20170804,35.9915,36.1001,35.6459,35.8434,20520301",
                                          "INTC,20170803,36.0902,36.1297,35.6953,36.0310,26611347",
                                          "INTC,20170802,35.6062,35.9394,35.3367,35.9100,35004283",
                                          "INTC,20170801,34.9495,35.7042,34.8613,35.6258,38710244",
                                          "INTC,20170731,34.7633,35.0280,34.6163,34.7633,27056989",
                                          "INTC,20170728,34.4301,35.1456,34.3027,34.6065,36291484",
                                          "INTC,20170727,34.0871,34.5477,33.9793,34.2733,43062842",
                                          "INTC,20170726,34.0087,34.2733,33.9009,34.0577,15499322",
                                          "INTC,20170725,33.8617,34.0430,33.7146,33.9793,18096682",
                                          "INTC,20170724,34.0381,34.1067,33.6950,33.8127,16146284",
                                          "INTC,20170721,33.8519,34.1263,33.7097,34.0381,22245041",
                                          "INTC,20170720,33.8519,34.1459,33.7882,34.0577,17506216",
                                          "INTC,20170719,33.9891,33.9891,33.7735,33.8715,17036197",
                                          "INTC,20170718,33.7440,33.8911,33.5676,33.8421,14362830",
                                          "INTC,20170717,34.0479,34.0479,33.6754,33.7833,21044407",
                                          "INTC,20170714,33.7931,34.0185,33.5823,33.9891,16316353",
                                          "INTC,20170713,33.6460,33.7146,33.4402,33.5578,15046199",
                                          "INTC,20170712,33.5970,33.7048,33.2638,33.5676,23768337",
                                          "INTC,20170711,32.9698,33.2540,32.7651,33.2442,25737540",
                                          "INTC,20170710,32.5876,33.0678,32.5680,32.9796,29918436",
                                          "INTC,20170707,33.0286,33.4402,33.0286,33.2050,18304460",
                                          "INTC,20170706,33.4402,33.6068,32.8914,32.9600,20733189",
                                          "INTC,20170705,32.8522,33.7440,32.8179,33.6558,30010803",
                                          "INTC,20170630,33.1952,33.2540,32.8669,33.0678,24432020",
                                          "INTC,20170629,33.2442,33.4206,32.6758,32.8718,25215664",
                                          "INTC,20170628,33.0972,33.5578,33.0678,33.5186,25940057",
                                          "INTC,20170627,33.3226,33.4647,32.9796,32.9796,27078918",
                                          "INTC,20170626,33.5774,33.8225,33.3520,33.3912,18854766",
                                          "INTC,20170623,33.5284,33.8617,33.4157,33.5088,29260888",
                                          "INTC,20170622,33.8715,33.9401,33.6068,33.6754,24602910",
                                          "INTC,20170621,33.6460,33.9107,33.4108,33.8911,27138493",
                                          "INTC,20170620,34.7437,34.7437,34.1459,34.1655,21536505",
                                          "INTC,20170619,34.9005,34.9005,34.6163,34.8025,21583752",
                                          "INTC,20170616,34.5771,34.6261,34.3125,34.5085,30762781",
                                          "INTC,20170615,34.5085,34.7731,34.4399,34.6065,20410009",
                                          "INTC,20170614,35.2730,35.3318,34.5183,34.8221,19149017",
                                          "INTC,20170613,34.9691,35.2338,34.8221,35.1652,20588247",
                                          "INTC,20170612,34.8809,35.2926,34.7045,35.0181,28488290",
                                          "INTC,20170609,35.7728,35.8316,34.6065,34.9985,33322053",
                                          "INTC,20170608,35.6160,35.8022,35.4396,35.7532,17154167",
                                          "INTC,20170607,35.4102,35.8414,35.3808,35.5376,17191983",
                                          "INTC,20170606,35.4396,35.7238,35.2632,35.4102,18420926",
                                          "INTC,20170605,35.5768,35.7728,35.4788,35.6160,11685699",
                                          "INTC,20170602,35.5474,35.6062,35.2828,35.5964,19127420",
                                          "INTC,20170601,35.4004,35.4102,35.0868,35.4004,17510915",
                                          "INTC,20170531,35.5866,35.6552,35.1554,35.3906,17741896",
                                          "INTC,20170530,35.5474,35.7140,35.3710,35.4592,13026915",
                                          "INTC,20170526,35.5376,35.6062,35.4102,35.5376,11145523",
                                          "INTC,20170525,35.4004,35.6650,35.3416,35.5376,13148542",
                                          "INTC,20170524,35.2632,35.4592,35.1750,35.4004,20860257",
                                          "INTC,20170523,35.1554,35.2730,34.8515,35.1456,16903704",
                                          "INTC,20170522,34.7731,35.3906,34.6751,35.0574,14638066",
                                          "INTC,20170519,34.4497,34.8417,34.4301,34.6947,18340127",
                                          "INTC,20170518,34.4889,34.7633,34.3811,34.5183,17171872",
                                          "INTC,20170517,34.9593,35.2726,34.3223,34.3419,26670188",
                                          "INTC,20170516,35.0378,35.1750,34.7143,35.1064,22291843",
                                          "INTC,20170515,34.8270,35.0034,34.7143,34.9201,22120137",
                                          "INTC,20170512,35.0083,35.0280,34.6947,34.8221,19730842",
                                          "INTC,20170511,35.1946,35.2828,34.7479,34.9789,21508923",
                                          "INTC,20170510,35.5474,35.6552,35.2142,35.2926,25133822",
                                          "INTC,20170509,35.7532,36.0178,35.5572,35.6454,17819144",
                                          "INTC,20170508,35.9982,36.1158,35.7532,35.8120,17660214",
                                          "INTC,20170505,36.1256,36.1942,35.7728,36.0864,18119243",
                                          "INTC,20170504,36.2530,36.3707,35.9100,36.1158,16437088",
                                          "INTC,20170503,35.9884,36.4295,35.8904,36.2432,22040256",
                                          "INTC,20170502,35.3827,36.0247,35.3632,35.9664,35813021",
                                          "INTC,20170501,35.1297,35.4118,34.9643,35.3243,24825614",
                                          "INTC,20170428,35.1200,35.4945,34.9060,35.1686,56769258",
                                          "INTC,20170427,35.8691,36.4820,35.7815,36.4139,39889967",
                                          "INTC,20170426,35.7815,36.2193,35.7815,35.9275,27240993",
                                          "INTC,20170425,35.7718,35.9956,35.6648,35.8691,22066776",
                                          "INTC,20170424,35.6745,35.8496,35.4702,35.7523,26305762",
                                          "INTC,20170421,35.2173,35.4313,34.8768,35.3340,20390513",
                                          "INTC,20170420,35.1978,35.2808,34.8865,35.1978,22968262",
                                          "INTC,20170419,34.9935,35.2367,34.9157,34.9352,16594635",
                                          "INTC,20170418,34.4682,34.8379,34.3368,34.7941,12321050",
                                          "INTC,20170417,34.4779,34.6238,34.3709,34.5168,12560746",
                                          "INTC,20170413,34.5849,34.7892,34.2931,34.2931,15763996",
                                          "INTC,20170412,34.8962,35.0324,34.5363,34.6628,19289787",
                                          "INTC,20170411,34.7990,34.8184,34.2542,34.7698,22711830",
                                          "INTC,20170410,35.0422,35.1103,34.6433,34.8281,19973723",
                                          "INTC,20170407,35.0908,35.3340,35.0081,35.0519,15689513",
                                          "INTC,20170406,35.1492,35.3000,35.0519,35.0519,20198223",
                                          "INTC,20170405,35.4410,35.7621,35.1881,35.2367,22455525",
                                          "INTC,20170404,35.1394,35.3875,35.0130,35.2951,18918045",
                                          "INTC,20170403,35.2075,35.7426,35.1394,35.1784,32013641",
                                          "INTC,20170331,34.7795,35.2659,34.7698,35.0908,21438385",
                                          "INTC,20170330,34.6141,34.9157,34.5557,34.7795,16432950",
                                          "INTC,20170329,34.5557,34.7309,34.4730,34.6044,17202642",
                                          "INTC,20170328,34.3806,34.7406,34.2542,34.6336,18398148",
                                          "INTC,20170327,34.1180,34.5363,33.9915,34.4293,16992235",
                                          "INTC,20170324,34.6822,34.7600,34.1666,34.2055,22187960",
                                          "INTC,20170323,34.5266,34.5266,34.0693,34.3125,20640352",
                                          "INTC,20170322,34.2639,34.4974,34.0499,34.4098,19144498",
                                          "INTC,20170321,34.6238,34.6336,34.0499,34.0888,22814547",
                                          "INTC,20170320,34.4098,34.6628,34.3028,34.4682,17322274",
                                          "INTC,20170317,34.3514,34.3709,34.1471,34.3125,30778867",
                                          "INTC,20170316,34.2152,34.4001,34.1180,34.1861,19616053",
                                          "INTC,20170315,33.9915,34.2152,33.7385,34.1471,27996095",
                                          "INTC,20170314,34.1082,34.2736,33.7191,34.2250,28586871",
                                          "INTC,20170313,34.8768,35.0616,33.9915,34.2055,53582931",
                                          "INTC,20170310,35.1394,35.3097,34.8087,34.9352,25402348",
                                          "INTC,20170309,34.6336,34.9643,34.5363,34.8476,23616786",
                                          "INTC,20170308,34.7017,34.9254,34.5606,34.6530,20920148",
                                          "INTC,20170307,34.5752,34.8281,34.4244,34.8281,23434893",
                                          "INTC,20170306,34.8184,34.8281,34.4585,34.6044,24416732"));
  }

  @Override
  protected URL expectedURL(final String symbol) throws MalformedURLException {
    return expectedURL(symbol, null);
  }

  @Override
  protected URL expectedURL(final String symbol, final Exchanges exchange)
      throws MalformedURLException {
    return expectedURL(symbol, null, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, null, null, null, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return expectedURL(symbol, null, null, null, frequency);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end)
      throws MalformedURLException {
    return expectedURL(symbol, null, null, null, null);
  }

  @Override
  protected URL expectedURL(final String symbol,
                            final Exchanges exchange,
                            final Calendar start,
                            final Calendar end,
                            final Frequencies frequency)
      throws MalformedURLException {
    return new URL(BASE + symbol +
                   ((frequency == Frequencies.WEEKLY) ? INTERVAL_WEEK_SPAN_5YEAR
                                                      : INTERVAL_DAY_SPAN_YEAR) +
                   SUFFIX);
  }

}