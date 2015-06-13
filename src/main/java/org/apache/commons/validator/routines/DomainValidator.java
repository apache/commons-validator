/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.net.IDN;
import java.util.Arrays;
import java.util.Locale;

/**
 * <p><b>Domain name</b> validation routines.</p>
 *
 * <p>
 * This validator provides methods for validating Internet domain names
 * and top-level domains.
 * </p>
 *
 * <p>Domain names are evaluated according
 * to the standards <a href="http://www.ietf.org/rfc/rfc1034.txt">RFC1034</a>,
 * section 3, and <a href="http://www.ietf.org/rfc/rfc1123.txt">RFC1123</a>,
 * section 2.1. No accommodation is provided for the specialized needs of
 * other applications; if the domain name has been URL-encoded, for example,
 * validation will fail even though the equivalent plaintext version of the
 * same name would have passed.
 * </p>
 *
 * <p>
 * Validation is also provided for top-level domains (TLDs) as defined and
 * maintained by the Internet Assigned Numbers Authority (IANA):
 * </p>
 *
 *   <ul>
 *     <li>{@link #isValidInfrastructureTld} - validates infrastructure TLDs
 *         (<code>.arpa</code>, etc.)</li>
 *     <li>{@link #isValidGenericTld} - validates generic TLDs
 *         (<code>.com, .org</code>, etc.)</li>
 *     <li>{@link #isValidCountryCodeTld} - validates country code TLDs
 *         (<code>.us, .uk, .cn</code>, etc.)</li>
 *   </ul>
 *
 * <p>
 * (<b>NOTE</b>: This class does not provide IP address lookup for domain names or
 * methods to ensure that a given domain name matches a specific IP; see
 * {@link java.net.InetAddress} for that functionality.)
 * </p>
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public class DomainValidator implements Serializable {

    private static final long serialVersionUID = -4407125112880174009L;

    // Regular expression strings for hostnames (derived from RFC2396 and RFC 1123)

    // RFC2396: domainlabel   = alphanum | alphanum *( alphanum | "-" ) alphanum
    // Max 63 characters
    private static final String DOMAIN_LABEL_REGEX = "\\p{Alnum}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?";

    // RFC2396 toplabel = alpha | alpha *( alphanum | "-" ) alphanum
    // Max 63 characters
    private static final String TOP_LABEL_REGEX = "\\p{Alpha}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?";

    // RFC2396 hostname = *( domainlabel "." ) toplabel [ "." ]
    // Note that the regex currently requires both a domain label and a top level label, whereas
    // the RFC does not. This is because the regex is used to detect if a TLD is present.
    // If the match fails, input is checked against DOMAIN_LABEL_REGEX (hostnameRegex)
    // RFC1123 sec 2.1 allows hostnames to start with a digit
    private static final String DOMAIN_NAME_REGEX =
            "^(?:" + DOMAIN_LABEL_REGEX + "\\.)+" + "(" + TOP_LABEL_REGEX + ")\\.?$";

    private final boolean allowLocal;

    /**
     * Singleton instance of this validator, which
     *  doesn't consider local addresses as valid.
     */
    private static final DomainValidator DOMAIN_VALIDATOR = new DomainValidator(false);

    /**
     * Singleton instance of this validator, which does
     *  consider local addresses valid.
     */
    private static final DomainValidator DOMAIN_VALIDATOR_WITH_LOCAL = new DomainValidator(true);

    /**
     * RegexValidator for matching domains.
     */
    private final RegexValidator domainRegex =
            new RegexValidator(DOMAIN_NAME_REGEX);
    /**
     * RegexValidator for matching a local hostname
     */
    // RFC1123 sec 2.1 allows hostnames to start with a digit
    private final RegexValidator hostnameRegex =
            new RegexValidator(DOMAIN_LABEL_REGEX);

    /**
     * Returns the singleton instance of this validator. It
     *  will not consider local addresses as valid.
     * @return the singleton instance of this validator
     */
    public static DomainValidator getInstance() {
        return DOMAIN_VALIDATOR;
    }

    /**
     * Returns the singleton instance of this validator,
     *  with local validation as required.
     * @param allowLocal Should local addresses be considered valid?
     * @return the singleton instance of this validator
     */
    public static DomainValidator getInstance(boolean allowLocal) {
       if(allowLocal) {
          return DOMAIN_VALIDATOR_WITH_LOCAL;
       }
       return DOMAIN_VALIDATOR;
    }

    /** Private constructor. */
    private DomainValidator(boolean allowLocal) {
       this.allowLocal = allowLocal;
    }

    /**
     * Returns true if the specified <code>String</code> parses
     * as a valid domain name with a recognized top-level domain.
     * The parsing is case-insensitive.
     * @param domain the parameter to check for domain name syntax
     * @return true if the parameter is a valid domain name
     */
    public boolean isValid(String domain) {
        if (domain == null) {
            return false;
        }
        domain = unicodeToASCII(domain);
        // hosts must be equally reachable via punycode and Unicode;
        // Unicode is never shorter than punycode, so check punycode
        // if domain did not convert, then it will be caught by ASCII
        // checks in the regexes below
        if (domain.length() > 253) {
            return false;
        }
        String[] groups = domainRegex.match(domain);
        if (groups != null && groups.length > 0) {
            return isValidTld(groups[0]);
        }
        return allowLocal && hostnameRegex.isValid(domain);
    }

    // package protected for unit test access
    // must agree with isValid() above
    final boolean isValidDomainSyntax(String domain) {
        if (domain == null) {
            return false;
        }
        domain = unicodeToASCII(domain);
        // hosts must be equally reachable via punycode and Unicode;
        // Unicode is never shorter than punycode, so check punycode
        // if domain did not convert, then it will be caught by ASCII
        // checks in the regexes below
        if (domain.length() > 253) {
            return false;
        }
        String[] groups = domainRegex.match(domain);
        return (groups != null && groups.length > 0)
                || hostnameRegex.isValid(domain);
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined top-level domain. Leading dots are ignored if present.
     * The search is case-insensitive.
     * @param tld the parameter to check for TLD status, not null
     * @return true if the parameter is a TLD
     */
    public boolean isValidTld(String tld) {
        tld = unicodeToASCII(tld);
        if(allowLocal && isValidLocalTld(tld)) {
           return true;
        }
        return isValidInfrastructureTld(tld)
                || isValidGenericTld(tld)
                || isValidCountryCodeTld(tld);
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined infrastructure top-level domain. Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param iTld the parameter to check for infrastructure TLD status, not null
     * @return true if the parameter is an infrastructure TLD
     */
    public boolean isValidInfrastructureTld(String iTld) {
        iTld = unicodeToASCII(iTld);
        return Arrays.binarySearch(INFRASTRUCTURE_TLDS, (chompLeadingDot(iTld.toLowerCase(Locale.ENGLISH)))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined generic top-level domain. Leading dots are ignored
     * if present. The search is case-insensitive.
     * @param gTld the parameter to check for generic TLD status, not null
     * @return true if the parameter is a generic TLD
     */
    public boolean isValidGenericTld(String gTld) {
        gTld = unicodeToASCII(gTld);
        return Arrays.binarySearch(GENERIC_TLDS, chompLeadingDot(gTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined country code top-level domain. Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param ccTld the parameter to check for country code TLD status, not null
     * @return true if the parameter is a country code TLD
     */
    public boolean isValidCountryCodeTld(String ccTld) {
        ccTld = unicodeToASCII(ccTld);
        return Arrays.binarySearch(COUNTRY_CODE_TLDS, chompLeadingDot(ccTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * widely used "local" domains (localhost or localdomain). Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param lTld the parameter to check for local TLD status, not null
     * @return true if the parameter is an local TLD
     */
    public boolean isValidLocalTld(String lTld) {
        lTld = unicodeToASCII(lTld);
        return Arrays.binarySearch(LOCAL_TLDS, chompLeadingDot(lTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    private String chompLeadingDot(String str) {
        if (str.startsWith(".")) {
            return str.substring(1);
        }
        return str;
    }

    // ---------------------------------------------
    // ----- TLDs defined by IANA
    // ----- Authoritative and comprehensive list at:
    // ----- http://data.iana.org/TLD/tlds-alpha-by-domain.txt

    // Note that the above list is in UPPER case.
    // The code currently converts strings to lower case (as per the tables below)

    // IANA also provide an HTML list at http://www.iana.org/domains/root/db
    // Note that this contains several country code entries which are NOT in
    // the text file. These all have the "Not assigned" in the "Sponsoring Organisation" column
    // For example (as of 2015-01-02):
    // .bl  country-code    Not assigned
    // .um  country-code    Not assigned

    // WARNING: this array MUST be sorted, others it cannot be searched reliably using binary search
    private static final String[] INFRASTRUCTURE_TLDS = new String[] {
        "arpa",               // internet infrastructure
    };

    // WARNING: this array MUST be sorted, others it cannot be searched reliably using binary search
    private static final String[] GENERIC_TLDS = new String[] {
        "abb", // abb ABB Ltd
        "abbott", // abbott Abbott Laboratories, Inc.
        "abogado",
        "academy",
        "accenture", // accenture Accenture plc
        "accountant", // accountant dot Accountant Limited
        "accountants",
        "active",
        "actor",
        "ads", // ads Charleston Road Registry Inc.
        "adult",
        "aero",
        "afl", // afl Australian Football League
        "agency",
        "aig", // aig American International Group, Inc.
        "airforce",
        "allfinanz",
        "alsace",
        "amsterdam",
        "android",
        "apartments", // apartments June Maple, LLC
        "aquarelle",
        "archi",
        "army",
        "arpa",
        "asia",
        "associates",
        "attorney",
        "auction",
        "audio",
        "auto", // auto Uniregistry, Corp.
        "autos",
        "axa",
        "azure", // azure Microsoft Corporation
        "band",
        "bank", // bank fTLD Registry Services, LLC
        "bar",
        "barclaycard", // barclaycard Barclays Bank PLC
        "barclays", // barclays Barclays Bank PLC
        "bargains",
        "bauhaus", // bauhaus Werkhaus GmbH
        "bayern",
        "bbc", // bbc British Broadcasting Corporation
        "bbva", // bbva BANCO BILBAO VIZCAYA ARGENTARIA, S.A.
        "beer",
        "berlin",
        "best",
        "bible", // bible American Bible Society
        "bid",
        "bike",
        "bing", // bing Microsoft Corporation
        "bingo", // bingo Sand Cedar, LLC
        "bio",
        "biz",
        "black",
        "blackfriday",
        "bloomberg",
        "blue",
        "bmw",
        "bnpparibas",
        "boats", // boats DERBoats, LLC
        "bond", // bond Bond University Limited
        "boo",
        "boutique",
        "bridgestone", // bridgestone Bridgestone Corporation
        "broker", // broker DOTBROKER REGISTRY LTD
        "brother", // brother Brother Industries, Ltd.
        "brussels",
        "budapest",
        "build",
        "builders",
        "business",
        "buzz",
        "bzh",
        "cab",
        "cafe", // cafe Pioneer Canyon, LLC
        "cal",
        "camera",
        "camp",
        "cancerresearch",
        "canon", // canon Canon Inc.
        "capetown",
        "capital",
        "caravan",
        "cards",
        "care",
        "career",
        "careers",
        "cars", // cars Uniregistry, Corp.
        "cartier",
        "casa",
        "cash",
        "casino", // casino Binky Sky, LLC
        "cat",
        "catering",
        "cbn", // cbn The Christian Broadcasting Network, Inc.
        "center",
        "ceo",
        "cern",
        "cfa", // cfa CFA Institute
        "cfd", // cfd DOTCFD REGISTRY LTD
        "channel",
        "chat", // chat Sand Fields, LLC
        "cheap",
        "chloe", // chloe Richemont DNS Inc.
        "christmas",
        "chrome",
        "church",
        "cisco", // cisco Cisco Technology, Inc.
        "citic",
        "city",
        "claims",
        "cleaning",
        "click",
        "clinic",
        "clothing",
        "club",
        "coach",
        "codes",
        "coffee",
        "college",
        "cologne",
        "com",
        "community",
        "company",
        "computer",
        "condos",
        "construction",
        "consulting",
        "contractors",
        "cooking",
        "cool",
        "coop",
        "corsica", // corsica Collectivité Territoriale de Corse
        "country",
        "coupons", // coupons Black Island, LLC
        "courses", // courses OPEN UNIVERSITIES AUSTRALIA PTY LTD
        "credit",
        "creditcard",
        "cricket",
        "crs",
        "cruises",
        "cuisinella",
        "cymru",
        "cyou", // cyou Beijing Gamease Age Digital Technology Co., Ltd.
        "dabur", // dabur Dabur India Limited
        "dad",
        "dance",
        "date", // date dot Date Limited
        "dating",
        "datsun", // datsun NISSAN MOTOR CO., LTD.
        "day",
        "dclk", // dclk Charleston Road Registry Inc.
        "deals",
        "degree",
        "delivery",
        "democrat",
        "dental",
        "dentist",
        "desi",
        "design", // design Top Level Design, LLC
        "dev",
        "diamonds",
        "diet",
        "digital",
        "direct",
        "directory",
        "discount",
        "dnp",
        "docs",
        "dog", // dog Koko Mill, LLC
        "doha", // doha Communications Regulatory Authority (CRA)
        "domains",
        "doosan",
        "download", // download dot Support Limited
        "durban",
        "dvag",
        "earth", // earth Interlink Co., Ltd.
        "eat",
        "edu",
        "education",
        "email",
        "emerck",
        "energy",
        "engineer",
        "engineering",
        "enterprises",
        "epson", // epson Seiko Epson Corporation
        "equipment",
        "erni", // erni ERNI Group Holding AG
        "esq",
        "estate",
        "eurovision",
        "eus",
        "events",
        "everbank",
        "exchange",
        "expert",
        "exposed",
        "express", // express Sea Sunset, LLC
        "fail",
        "faith", // faith dot Faith Limited
        "fan", // fan Asiamix Digital Ltd
        "fans", // fans Asiamix Digital Limited
        "farm",
        "fashion",
        "feedback",
        "film", // film Motion Picture Domain Registry Pty Ltd
        "finance",
        "financial",
        "firmdale",
        "fish",
        "fishing",
        "fit", // fit Minds + Machines Group Limited
        "fitness",
        "flights",
        "florist",
        "flowers",
        "flsmidth",
        "fly",
        "foo",
        "football", // football Foggy Farms, LLC
        "forex", // forex DOTFOREX REGISTRY LTD
        "forsale",
        "foundation",
        "frl",
        "frogans",
        "fund",
        "furniture",
        "futbol",
        "fyi", // fyi Silver Tigers, LLC
        "gal",
        "gallery",
        "garden",
        "gbiz",
        "gdn", // gdn Joint Stock Company &quot;Navigation-information systems&quot;
        "gent",
        "ggee",
        "gift",
        "gifts",
        "gives",
        "glass",
        "gle",
        "global",
        "globo",
        "gmail",
        "gmo",
        "gmx",
        "gold", // gold June Edge, LLC
        "goldpoint", // goldpoint YODOBASHI CAMERA CO.,LTD.
        "golf", // golf Lone Falls, LLC
        "goo", // goo NTT Resonant Inc.
        "goog", // goog Charleston Road Registry Inc.
        "google",
        "gop",
        "gov",
        "graphics",
        "gratis",
        "green",
        "gripe",
        "guge", // guge Charleston Road Registry Inc.
        "guide",
        "guitars",
        "guru",
        "hamburg",
        "hangout", // hangout Charleston Road Registry Inc.
        "haus",
        "healthcare",
        "help",
        "here",
        "hermes", // hermes Hermes International
        "hiphop",
        "hitachi", // hitachi Hitachi, Ltd.
        "hiv",
        "hockey", // hockey Half Willow, LLC
        "holdings",
        "holiday",
        "homedepot", // homedepot Homer TLC, Inc.
        "homes",
        "honda", // honda Honda Motor Co., Ltd.
        "horse",
        "host",
        "hosting",
        "hotmail", // hotmail Microsoft Corporation
        "house",
        "how",
        "ibm",
        "icbc", // icbc Industrial and Commercial Bank of China Limited
        "icu", // icu One.com A/S
        "ifm", // ifm ifm electronic gmbh
        "immo",
        "immobilien",
        "industries",
        "infiniti", // infiniti NISSAN MOTOR CO., LTD.
        "info",
        "ing",
        "ink",
        "institute",
        "insure",
        "int",
        "international",
        "investments",
        "irish",
        "iwc",
        "java", // java Oracle Corporation
        "jcb", // jcb JCB Co., Ltd.
        "jetzt",
        "jewelry", // jewelry Wild Bloom, LLC
        "jlc", // jlc Richemont DNS Inc.
        "jll", // jll Jones Lang LaSalle Incorporated
        "jobs",
        "joburg",
        "juegos",
        "kaufen",
        "kddi", // kddi KDDI CORPORATION
        "kim",
        "kitchen",
        "kiwi",
        "koeln",
        "komatsu", // komatsu Komatsu Ltd.
        "krd",
        "kred",
        "kyoto", // kyoto Academic Institution: Kyoto Jyoho Gakuen
        "lacaixa",
        "land",
        "lasalle", // lasalle Jones Lang LaSalle Incorporated
        "lat", // lat ECOM-LAC Federación de Latinoamérica y el Caribe para Internet y el Comercio Electrónico
        "latrobe",
        "lawyer",
        "lds",
        "lease",
        "leclerc", // leclerc A.C.D. LEC Association des Centres Distributeurs Edouard Leclerc
        "legal",
        "lgbt",
        "liaison", // liaison Liaison Technologies, Incorporated
        "lidl",
        "life",
        "lighting",
        "limited",
        "limo",
        "link",
        "loan", // loan dot Loan Limited
        "loans",
        "lol", // lol Uniregistry, Corp.
        "london",
        "lotte", // lotte Lotte Holdings Co., Ltd.
        "lotto",
        "love", // love Merchant Law Group LLP
        "ltda",
        "lupin", // lupin LUPIN LIMITED
        "luxe",
        "luxury",
        "madrid",
        "maif", // maif Mutuelle Assurance Instituteur France (MAIF)
        "maison",
        "management",
        "mango",
        "market",
        "marketing",
        "markets", // markets DOTMARKETS REGISTRY LTD
        "marriott", // marriott Marriott Worldwide Corporation
        "mba", // mba Lone Hollow, LLC
        "media",
        "meet",
        "melbourne",
        "meme",
        "memorial",
        "men", // men Exclusive Registry Limited
        "menu",
        "miami",
        "microsoft", // microsoft Microsoft Corporation
        "mil",
        "mini",
        "mma", // mma MMA IARD
        "mobi",
        "moda",
        "moe",
        "monash",
        "money",
        "montblanc", // montblanc Richemont DNS Inc.
        "mormon",
        "mortgage",
        "moscow",
        "motorcycles",
        "mov",
        "movie", // movie New Frostbite, LLC
        "mtn", // mtn MTN Dubai Limited
        "mtpc", // mtpc Mitsubishi Tanabe Pharma Corporation
        "museum",
        "nadex", // nadex Nadex Domains, Inc
        "nagoya",
        "name",
        "navy",
        "nec", // nec NEC Corporation
        "net",
        "network",
        "neustar",
        "new",
        "news", // news United TLD Holdco Ltd.
        "nexus",
        "ngo",
        "nhk",
        "nico", // nico DWANGO Co., Ltd.
        "ninja",
        "nissan", // nissan NISSAN MOTOR CO., LTD.
        "nra",
        "nrw",
        "ntt", // ntt NIPPON TELEGRAPH AND TELEPHONE CORPORATION
        "nyc",
        "okinawa",
        "one", // one One.com A/S
        "ong",
        "onl",
        "online", // online DotOnline Inc.
        "ooo",
        "org",
        "organic",
        "osaka",
        "otsuka",
        "ovh",
        "page", // page Charleston Road Registry Inc.
        "panerai", // panerai Richemont DNS Inc.
        "paris",
        "partners",
        "parts",
        "party",
        "pharmacy",
        "philips", // philips Koninklijke Philips N.V.
        "photo",
        "photography",
        "photos",
        "physio",
        "piaget", // piaget Richemont DNS Inc.
        "pics",
        "pictet", // pictet Pictet Europe S.A.
        "pictures",
        "pink",
        "pizza",
        "place",
        "plumbing",
        "plus", // plus Sugar Mill, LLC
        "pohl",
        "poker",
        "porn",
        "post",
        "praxi",
        "press",
        "pro",
        "prod",
        "productions",
        "prof",
        "properties",
        "property",
        "pub",
        "qpon",
        "quebec",
        "racing", // racing Premier Registry Limited
        "realtor",
        "recipes",
        "red",
        "redstone", // redstone Redstone Haute Couture Co., Ltd.
        "rehab",
        "reise",
        "reisen",
        "reit",
        "ren",
        "rent", // rent XYZ.COM LLC
        "rentals",
        "repair",
        "report",
        "republican",
        "rest",
        "restaurant",
        "review", // review dot Review Limited
        "reviews",
        "rich",
        "rio",
        "rip",
        "rocks",
        "rodeo",
        "rsvp",
        "ruhr",
        "run", // run Snow Park, LLC
        "ryukyu",
        "saarland",
        "sale",
        "samsung",
        "sandvik", // sandvik Sandvik AB
        "sandvikcoromant", // sandvikcoromant Sandvik AB
        "sap", // sap SAP AG
        "sarl",
        "saxo", // saxo Saxo Bank A/S
        "sca",
        "scb",
        "schmidt",
        "scholarships", // scholarships Scholarships.com, LLC
        "school", // school Little Galley, LLC
        "schule",
        "schwarz",
        "science",
        "scot",
        "seat", // seat SEAT, S.A. (Sociedad Unipersonal)
        "sener", // sener Sener Ingeniería y Sistemas, S.A.
        "services",
        "sew",
        "sex", // sex ICM Registry SX LLC
        "sexy",
        "shiksha",
        "shoes",
        "show", // show Snow Beach, LLC
        "shriram",
        "singles",
        "site", // site DotSite Inc.
        "ski", // ski STARTING DOT LIMITED
        "sky",
        "sncf", // sncf SNCF (Société Nationale des Chemins de fer Francais)
        "soccer", // soccer Foggy Shadow, LLC
        "social",
        "software",
        "sohu",
        "solar",
        "solutions",
        "sony", // sony Sony Corporation
        "soy",
        "space",
        "spiegel",
        "spreadbetting", // spreadbetting DOTSPREADBETTING REGISTRY LTD
        "study", // study OPEN UNIVERSITIES AUSTRALIA PTY LTD
        "style", // style Binky Moon, LLC
        "sucks", // sucks Vox Populi Registry Inc.
        "supplies",
        "supply",
        "support",
        "surf",
        "surgery",
        "suzuki",
        "swiss", // swiss Swiss Confederation
        "sydney",
        "systems",
        "taipei",
        "tatar",
        "tattoo",
        "tax",
        "taxi", // taxi Pine Falls, LLC
        "team", // team Atomic Lake, LLC
        "tech", // tech Dot Tech LLC
        "technology",
        "tel",
        "temasek", // temasek Temasek Holdings (Private) Limited
        "tennis", // tennis Cotton Bloom, LLC
        "thd", // thd Homer TLC, Inc.
        "theater", // theater Blue Tigers, LLC
        "tickets", // tickets Accent Media Limited
        "tienda",
        "tips",
        "tires",
        "tirol",
        "today",
        "tokyo",
        "tools",
        "top",
        "toray", // toray Toray Industries, Inc.
        "toshiba", // toshiba TOSHIBA Corporation
        "tours", // tours Sugar Station, LLC
        "town",
        "toys",
        "trade",
        "trading", // trading DOTTRADING REGISTRY LTD
        "training",
        "travel",
        "trust",
        "tui",
        "university",
        "uno",
        "uol",
        "vacations",
        "vegas",
        "ventures",
        "versicherung",
        "vet",
        "viajes",
        "video",
        "villas",
        "vision",
        "vlaanderen",
        "vodka",
        "vote",
        "voting",
        "voto",
        "voyage",
        "wales",
        "walter", // walter Sandvik AB
        "wang",
        "watch",
        "webcam",
        "website",
        "wed",
        "wedding",
        "weir", // weir Weir Group IP Limited
        "whoswho",
        "wien",
        "wiki",
        "williamhill",
        "win", // win First Registry Limited
        "windows", // windows Microsoft Corporation
        "wme",
        "work",
        "works",
        "world",
        "wtc",
        "wtf",
        "xbox", // xbox Microsoft Corporation
        "xerox", // xerox Xerox DNHC LLC
        "xin", // xin Elegant Leader Limited
        "xn--1qqw23a", // 佛山 Guangzhou YU Wei Information Technology Co., Ltd.
        "xn--30rr7y", // ?? Excellent First Limited
        "xn--3bst00m", // 集团 Eagle Horizon Limited
        "xn--3ds443g", // 在线 TLD REGISTRY LIMITED
        "xn--45q11c", // 八卦 Zodiac Scorpio Limited
        "xn--4gbrim", // موقع Suhub Electronic Establishment
        "xn--55qw42g", // 公益 China Organizational Name Administration Center
        "xn--55qx5d", // 公司 Computer Network Information Center of Chinese Academy of Sciences （China Internet Network Information Center）
        "xn--6frz82g", // 移动 Afilias Limited
        "xn--6qq986b3xl", // 我爱你 Tycoon Treasure Limited
        "xn--80adxhks", // москва Foundation for Assistance for Internet Technologies and Infrastructure Development (FAITID)
        "xn--80asehdb", // онлайн CORE Association
        "xn--80aswg", // сайт CORE Association
        "xn--9et52u", // ?? RISE VICTORY LIMITED
        "xn--b4w605ferd", // ??? Temasek Holdings (Private) Limited
        "xn--c1avg", // орг Public Interest Registry
        "xn--cg4bki", // 삼성 SAMSUNG SDS CO., LTD
        "xn--czr694b", // 商标 HU YI GLOBAL INFORMATION RESOURCES(HOLDING) COMPANY.HONGKONG LIMITED
        "xn--czrs0t", // 商店 Wild Island, LLC
        "xn--czru2d", // 商城 Zodiac Aquarius Limited
        "xn--d1acj3b", // дети The Foundation for Network Initiatives “The Smart Internet”
        "xn--estv75g", // ?? Industrial and Commercial Bank of China Limited
        "xn--fiq228c5hs", // 中文网 TLD REGISTRY LIMITED
        "xn--fiq64b", // 中信 CITIC Group Corporation
        "xn--fjq720a", // ?? Will Bloom, LLC
        "xn--flw351e", // 谷歌 Charleston Road Registry Inc.
        "xn--hxt814e", // 网店 Zodiac Libra Limited
        "xn--i1b6b1a6a2e", // संगठन Public Interest Registry
        "xn--imr513n", // ?? HU YI GLOBAL INFORMATION RESOURCES (HOLDING) COMPANY. HONGKONG LIMITED
        "xn--io0a7i", // 网络 Computer Network Information Center of Chinese Academy of Sciences （China Internet Network Information Center）
        "xn--kcrx77d1x4a", // ??? Koninklijke Philips N.V.
        "xn--kput3i", // 手机 Beijing RITT-Net Technology Development Co., Ltd
        "xn--mgbab2bd", // بازار CORE Association
        "xn--mxtq1m", // ?? Net-Chinese Co., Ltd.
        "xn--ngbc5azd", // شبكة International Domain Registry Pty. Ltd.
        "xn--nqv7f", // 机构 Public Interest Registry
        "xn--nqv7fs00ema", // 组织机构 Public Interest Registry
        "xn--nyqy26a", // ?? Stable Tone Limited
        "xn--p1acf", // рус Rusnames Limited
        "xn--q9jyb4c", // みんな Charleston Road Registry Inc.
        "xn--qcka1pmc", // グーグル Charleston Road Registry Inc.
        "xn--rhqv96g", // 世界 Stable Tone Limited
        "xn--ses554g", // 网址 HU YI GLOBAL INFORMATION RESOURCES (HOLDING) COMPANY. HONGKONG LIMITED
        "xn--unup4y", // 游戏 Spring Fields, LLC
        "xn--vermgensberater-ctb", // vermögensberater Deutsche Vermögensberatung Aktiengesellschaft DVAG
        "xn--vermgensberatung-pwb", // vermögensberatung Deutsche Vermögensberatung Aktiengesellschaft DVAG
        "xn--vhquv", // 企业 Dash McCook, LLC
        "xn--vuq861b", // ?? Beijing Tele-info Network Technology Co., Ltd.
        "xn--xhq521b", // 广东 Guangzhou YU Wei Information Technology Co., Ltd.
        "xn--zfr164b", // 政务 China Organizational Name Administration Center
        "xxx",
        "xyz",
        "yachts",
        "yandex",
        "yodobashi", // yodobashi YODOBASHI CAMERA CO.,LTD.
        "yoga",
        "yokohama",
        "youtube",
        "zip",
        "zone",
        "zuerich",
   };

    // WARNING: this array MUST be sorted, others it cannot be searched reliably using binary search
    private static final String[] COUNTRY_CODE_TLDS = new String[] {
        "ac",                 // Ascension Island
        "ad",                 // Andorra
        "ae",                 // United Arab Emirates
        "af",                 // Afghanistan
        "ag",                 // Antigua and Barbuda
        "ai",                 // Anguilla
        "al",                 // Albania
        "am",                 // Armenia
        "an",                 // Netherlands Antilles
        "ao",                 // Angola
        "aq",                 // Antarctica
        "ar",                 // Argentina
        "as",                 // American Samoa
        "at",                 // Austria
        "au",                 // Australia (includes Ashmore and Cartier Islands and Coral Sea Islands)
        "aw",                 // Aruba
        "ax",                 // Åland
        "az",                 // Azerbaijan
        "ba",                 // Bosnia and Herzegovina
        "bb",                 // Barbados
        "bd",                 // Bangladesh
        "be",                 // Belgium
        "bf",                 // Burkina Faso
        "bg",                 // Bulgaria
        "bh",                 // Bahrain
        "bi",                 // Burundi
        "bj",                 // Benin
        "bm",                 // Bermuda
        "bn",                 // Brunei Darussalam
        "bo",                 // Bolivia
        "br",                 // Brazil
        "bs",                 // Bahamas
        "bt",                 // Bhutan
        "bv",                 // Bouvet Island
        "bw",                 // Botswana
        "by",                 // Belarus
        "bz",                 // Belize
        "ca",                 // Canada
        "cc",                 // Cocos (Keeling) Islands
        "cd",                 // Democratic Republic of the Congo (formerly Zaire)
        "cf",                 // Central African Republic
        "cg",                 // Republic of the Congo
        "ch",                 // Switzerland
        "ci",                 // Côte d'Ivoire
        "ck",                 // Cook Islands
        "cl",                 // Chile
        "cm",                 // Cameroon
        "cn",                 // China, mainland
        "co",                 // Colombia
        "cr",                 // Costa Rica
        "cu",                 // Cuba
        "cv",                 // Cape Verde
        "cw",                 // Curaçao
        "cx",                 // Christmas Island
        "cy",                 // Cyprus
        "cz",                 // Czech Republic
        "de",                 // Germany
        "dj",                 // Djibouti
        "dk",                 // Denmark
        "dm",                 // Dominica
        "do",                 // Dominican Republic
        "dz",                 // Algeria
        "ec",                 // Ecuador
        "ee",                 // Estonia
        "eg",                 // Egypt
        "er",                 // Eritrea
        "es",                 // Spain
        "et",                 // Ethiopia
        "eu",                 // European Union
        "fi",                 // Finland
        "fj",                 // Fiji
        "fk",                 // Falkland Islands
        "fm",                 // Federated States of Micronesia
        "fo",                 // Faroe Islands
        "fr",                 // France
        "ga",                 // Gabon
        "gb",                 // Great Britain (United Kingdom)
        "gd",                 // Grenada
        "ge",                 // Georgia
        "gf",                 // French Guiana
        "gg",                 // Guernsey
        "gh",                 // Ghana
        "gi",                 // Gibraltar
        "gl",                 // Greenland
        "gm",                 // The Gambia
        "gn",                 // Guinea
        "gp",                 // Guadeloupe
        "gq",                 // Equatorial Guinea
        "gr",                 // Greece
        "gs",                 // South Georgia and the South Sandwich Islands
        "gt",                 // Guatemala
        "gu",                 // Guam
        "gw",                 // Guinea-Bissau
        "gy",                 // Guyana
        "hk",                 // Hong Kong
        "hm",                 // Heard Island and McDonald Islands
        "hn",                 // Honduras
        "hr",                 // Croatia (Hrvatska)
        "ht",                 // Haiti
        "hu",                 // Hungary
        "id",                 // Indonesia
        "ie",                 // Ireland (Éire)
        "il",                 // Israel
        "im",                 // Isle of Man
        "in",                 // India
        "io",                 // British Indian Ocean Territory
        "iq",                 // Iraq
        "ir",                 // Iran
        "is",                 // Iceland
        "it",                 // Italy
        "je",                 // Jersey
        "jm",                 // Jamaica
        "jo",                 // Jordan
        "jp",                 // Japan
        "ke",                 // Kenya
        "kg",                 // Kyrgyzstan
        "kh",                 // Cambodia (Khmer)
        "ki",                 // Kiribati
        "km",                 // Comoros
        "kn",                 // Saint Kitts and Nevis
        "kp",                 // North Korea
        "kr",                 // South Korea
        "kw",                 // Kuwait
        "ky",                 // Cayman Islands
        "kz",                 // Kazakhstan
        "la",                 // Laos (currently being marketed as the official domain for Los Angeles)
        "lb",                 // Lebanon
        "lc",                 // Saint Lucia
        "li",                 // Liechtenstein
        "lk",                 // Sri Lanka
        "lr",                 // Liberia
        "ls",                 // Lesotho
        "lt",                 // Lithuania
        "lu",                 // Luxembourg
        "lv",                 // Latvia
        "ly",                 // Libya
        "ma",                 // Morocco
        "mc",                 // Monaco
        "md",                 // Moldova
        "me",                 // Montenegro
        "mg",                 // Madagascar
        "mh",                 // Marshall Islands
        "mk",                 // Republic of Macedonia
        "ml",                 // Mali
        "mm",                 // Myanmar
        "mn",                 // Mongolia
        "mo",                 // Macau
        "mp",                 // Northern Mariana Islands
        "mq",                 // Martinique
        "mr",                 // Mauritania
        "ms",                 // Montserrat
        "mt",                 // Malta
        "mu",                 // Mauritius
        "mv",                 // Maldives
        "mw",                 // Malawi
        "mx",                 // Mexico
        "my",                 // Malaysia
        "mz",                 // Mozambique
        "na",                 // Namibia
        "nc",                 // New Caledonia
        "ne",                 // Niger
        "nf",                 // Norfolk Island
        "ng",                 // Nigeria
        "ni",                 // Nicaragua
        "nl",                 // Netherlands
        "no",                 // Norway
        "np",                 // Nepal
        "nr",                 // Nauru
        "nu",                 // Niue
        "nz",                 // New Zealand
        "om",                 // Oman
        "pa",                 // Panama
        "pe",                 // Peru
        "pf",                 // French Polynesia With Clipperton Island
        "pg",                 // Papua New Guinea
        "ph",                 // Philippines
        "pk",                 // Pakistan
        "pl",                 // Poland
        "pm",                 // Saint-Pierre and Miquelon
        "pn",                 // Pitcairn Islands
        "pr",                 // Puerto Rico
        "ps",                 // Palestinian territories (PA-controlled West Bank and Gaza Strip)
        "pt",                 // Portugal
        "pw",                 // Palau
        "py",                 // Paraguay
        "qa",                 // Qatar
        "re",                 // Réunion
        "ro",                 // Romania
        "rs",                 // Serbia
        "ru",                 // Russia
        "rw",                 // Rwanda
        "sa",                 // Saudi Arabia
        "sb",                 // Solomon Islands
        "sc",                 // Seychelles
        "sd",                 // Sudan
        "se",                 // Sweden
        "sg",                 // Singapore
        "sh",                 // Saint Helena
        "si",                 // Slovenia
        "sj",                 // Svalbard and Jan Mayen Islands Not in use (Norwegian dependencies; see .no)
        "sk",                 // Slovakia
        "sl",                 // Sierra Leone
        "sm",                 // San Marino
        "sn",                 // Senegal
        "so",                 // Somalia
        "sr",                 // Suriname
        "st",                 // São Tomé and Príncipe
        "su",                 // Soviet Union (deprecated)
        "sv",                 // El Salvador
        "sx",                 // Sint Maarten
        "sy",                 // Syria
        "sz",                 // Swaziland
        "tc",                 // Turks and Caicos Islands
        "td",                 // Chad
        "tf",                 // French Southern and Antarctic Lands
        "tg",                 // Togo
        "th",                 // Thailand
        "tj",                 // Tajikistan
        "tk",                 // Tokelau
        "tl",                 // East Timor (deprecated old code)
        "tm",                 // Turkmenistan
        "tn",                 // Tunisia
        "to",                 // Tonga
//        "tp",                 // East Timor (Retired)
        "tr",                 // Turkey
        "tt",                 // Trinidad and Tobago
        "tv",                 // Tuvalu
        "tw",                 // Taiwan, Republic of China
        "tz",                 // Tanzania
        "ua",                 // Ukraine
        "ug",                 // Uganda
        "uk",                 // United Kingdom
        "us",                 // United States of America
        "uy",                 // Uruguay
        "uz",                 // Uzbekistan
        "va",                 // Vatican City State
        "vc",                 // Saint Vincent and the Grenadines
        "ve",                 // Venezuela
        "vg",                 // British Virgin Islands
        "vi",                 // U.S. Virgin Islands
        "vn",                 // Vietnam
        "vu",                 // Vanuatu
        "wf",                 // Wallis and Futuna
        "ws",                 // Samoa (formerly Western Samoa)
        "xn--3e0b707e", // 한국 KISA (Korea Internet &amp; Security Agency)
        "xn--45brj9c", // ভারত National Internet Exchange of India
        "xn--80ao21a", // қаз Association of IT Companies of Kazakhstan
        "xn--90a3ac", // срб Serbian National Internet Domain Registry (RNIDS)
        "xn--90ais", // ??? Reliable Software Inc.
        "xn--clchc0ea0b2g2a9gcd", // சிங்கப்பூர் Singapore Network Information Centre (SGNIC) Pte Ltd
        "xn--d1alf", // мкд Macedonian Academic Research Network Skopje
        "xn--fiqs8s", // 中国 China Internet Network Information Center
        "xn--fiqz9s", // 中國 China Internet Network Information Center
        "xn--fpcrj9c3d", // భారత్ National Internet Exchange of India
        "xn--fzc2c9e2c", // ලංකා LK Domain Registry
        "xn--gecrj9c", // ભારત National Internet Exchange of India
        "xn--h2brj9c", // भारत National Internet Exchange of India
        "xn--j1amh", // укр Ukrainian Network Information Centre (UANIC), Inc.
        "xn--j6w193g", // 香港 Hong Kong Internet Registration Corporation Ltd.
        "xn--kprw13d", // 台湾 Taiwan Network Information Center (TWNIC)
        "xn--kpry57d", // 台灣 Taiwan Network Information Center (TWNIC)
        "xn--l1acc", // мон Datacom Co.,Ltd
        "xn--lgbbat1ad8j", // الجزائر CERIST
        "xn--mgb9awbf", // عمان Telecommunications Regulatory Authority (TRA)
        "xn--mgba3a4f16a", // ایران Institute for Research in Fundamental Sciences (IPM)
        "xn--mgbaam7a8h", // امارات Telecommunications Regulatory Authority (TRA)
        "xn--mgbayh7gpa", // الاردن National Information Technology Center (NITC)
        "xn--mgbbh1a71e", // بھارت National Internet Exchange of India
        "xn--mgbc0a9azcg", // المغرب Agence Nationale de Réglementation des Télécommunications (ANRT)
        "xn--mgberp4a5d4ar", // السعودية Communications and Information Technology Commission
        "xn--mgbpl2fh", // ????? Sudan Internet Society
        "xn--mgbx4cd0ab", // مليسيا MYNIC Berhad
        "xn--node", // გე Information Technologies Development Center (ITDC)
        "xn--o3cw4h", // ไทย Thai Network Information Center Foundation
        "xn--ogbpf8fl", // سورية National Agency for Network Services (NANS)
        "xn--p1ai", // рф Coordination Center for TLD RU
        "xn--pgbs0dh", // تونس Agence Tunisienne d&#39;Internet
        "xn--s9brj9c", // ਭਾਰਤ National Internet Exchange of India
        "xn--wgbh1c", // مصر National Telecommunication Regulatory Authority - NTRA
        "xn--wgbl6a", // قطر Communications Regulatory Authority
        "xn--xkc2al3hye2a", // இலங்கை LK Domain Registry
        "xn--xkc2dl3a5ee0h", // இந்தியா National Internet Exchange of India
        "xn--y9a3aq", // ??? Internet Society
        "xn--yfro4i67o", // 新加坡 Singapore Network Information Centre (SGNIC) Pte Ltd
        "xn--ygbi2ammx", // فلسطين Ministry of Telecom &amp; Information Technology (MTIT)
        "ye",                 // Yemen
        "yt",                 // Mayotte
        "za",                 // South Africa
        "zm",                 // Zambia
        "zw",                 // Zimbabwe
    };

    // WARNING: this array MUST be sorted, others it cannot be searched reliably using binary search
    private static final String[] LOCAL_TLDS = new String[] {
       "localdomain",         // Also widely used as localhost.localdomain
       "localhost",           // RFC2606 defined
    };

    /**
     * Converts potentially Unicode input to punycode.
     * If conversion fails, returns the original input.
     * 
     * @param input the string to convert, not null
     * @return converted input, or original input if conversion fails
     */
    // Needed by UrlValidator
    static String unicodeToASCII(String input) {
        if (isOnlyASCII(input)) { // skip possibly expensive processing
            return input;
        }
        try {
            final String ascii = IDN.toASCII(input);
            if (IDNBUGHOLDER.IDN_TOASCII_PRESERVES_TRAILING_DOTS) {
                return ascii;
            }
            final int length = input.length();
            if (length == 0) {// check there is a last character
                return input;
            }
// RFC3490 3.1. 1)
//            Whenever dots are used as label separators, the following
//            characters MUST be recognized as dots: U+002E (full stop), U+3002
//            (ideographic full stop), U+FF0E (fullwidth full stop), U+FF61
//            (halfwidth ideographic full stop).
            char lastChar = input.charAt(length-1);// fetch original last char
            switch(lastChar) {
                case '\u002E': // "." full stop
                case '\u3002': // ideographic full stop
                case '\uFF0E': // fullwidth full stop
                case '\uFF61': // halfwidth ideographic full stop
                    return ascii + "."; // restore the missing stop
                default:
                    return ascii;
            }
        } catch (IllegalArgumentException e) { // input is not valid
            return input;
        }
    }

    private static class IDNBUGHOLDER {
        private static boolean keepsTrailingDot() {
            final String input = "a."; // must be a valid name
            return input.equals(IDN.toASCII(input));
        }
        private static final boolean IDN_TOASCII_PRESERVES_TRAILING_DOTS = keepsTrailingDot();
    }

    /*
     * Check if input contains only ASCII
     * Treats null as all ASCII
     */
    private static boolean isOnlyASCII(String input) {
        if (input == null) {
            return true;
        }
        for(int i=0; i < input.length(); i++) {
            if (input.charAt(i) > 0x7F) {
                return false;
            }
        }
        return true;
    }
}
