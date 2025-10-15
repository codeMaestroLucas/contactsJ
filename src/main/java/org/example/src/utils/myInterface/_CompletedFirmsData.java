package org.example.src.utils.myInterface;

import lombok.Getter;
import org.example.src.entities.BaseSites.Site;
import org.example.src.sites.byNewPage.*;
import org.example.src.sites.byPage.*;
import org.example.src.sites.to_test.*;

@Getter
public class _CompletedFirmsData {

    public final static Site[] byPage = {
            new AddleshawGoddardLLP(), new Aera(), new AGPAdvokater(), new Alliotts(), new Amorys(),
            new Andersen(), new ApplebyGlobal(), new AraozAndRueda(), new ArnesenIP(), new ArnoldAndPorter(),
            new AronTadmorLevy(), new Ashitiva(), new Ashurst(), new AWA(), new BAHR(),
            new BancilaDiaconuSiAsociatii(), new BankiHaddockFiora(), new BARDEHLEPAGENBERG(), new BARENTSKRANS(), new BarristonLaw(),
            new BennettJones(), new BlakeMorgan(), new Blakes(), new BlandyAndBlandy(), new BLGLaw(),
            new BNT(), new BonelliErede(), new BoodleHatfield(), new BrinkmannAndPartner(), new Broseta(),
            new BullAndCo(), new BurnetDuckworthAndPalmer(), new BussMurtonLaw(), new ByrneWallace(), new CampbellsLegal(),
            new CareyOlsen(), new Cassels(), new CassidyLevyKent(), new CastrenAndSnellman(), new CBA(),
            new Chattertons(), new Cirio(), new Clarkslegal(), new ClarkWilson(), new ClemensLaw(),
            new CliffordChance(), new CLPLaw(), new Codex(), new CollasCrill(), new Conyers(),
            new CovingtonAndBurlingLLP(), new CrowellAndMoring(), new CWAAssociates(), new DahlLaw(), new DaleAndLessmann(),
            new DechertLLP(), new Delcade(), new DillonEustace(), new DimitrovPetrovAndCo(), new DittmarAndIndrenius(),
            new Dompatent(), new DrzewieckiTomaszek(), new DZPLaw(), new ECLegalRubio(), new FaegreDrinkerBiddleAndReath(),
            new Fidal(), new FilipAndCompany(), new FillmoreRiley(), new Finreg360(), new FisherQuarmbyAndPfeifer(),
            new FIVERS(), new FluegelPreissner(), new FoglerRubinoff(), new Foyen(), new GanadoAdvocates(),
            new GarciaBodan(), new GianniAndOrigoni(), new GomezAceboAndPombo(), new Goodmans(), new GorrissenFederspiel(),
            new GreenbergTraurig(), new Haavind(), new HabrakenRutten(), new HammarskioldAndCo(), new HannesSnellman(),
            new HavelPartners(), new Hayes(), new HaynesAndBoone(), new HBNLaw(), new HerbertSmithFreehillsKramer(),
            new HFAndCo(), new HFW(), new HillDickinson(), new HjulmandCaptain(), new HNA(),
            new HollandAndKnight(), new Holst(), new HPPAttorneys(), new Hugel(), new JGSA(),
            new JoffeAndAssocies(), new JoksovicStojanovicAndPartners(), new JonesDay(), new Kanter(), new KantorAndImmerman(),
            new KewLaw(), new KienhuisLegal(), new Kinstellar(), new KISCHIP(), new KnezovicAndAssociates(),
            new KochanskiAndPartners(), new Kolster(), new Krogerus(), new KromannReumert(), new Langlois(),
            new LaszczukAndWspolnicy(), new LathamAndWatkins(), new LawsonLundell(), new LEFOSSE(), new LEGlobal(),
            new LemstraVanDerKorst(), new LEXIA(), new LEXLogmannsstofa(), new Liedekerke(), new Logos(),
            new LoopstraNixon(), new LPAGGV(), new LPALaw(), new Lydian(), new MagnussonLaw(),
            new MAQS(), new Matheson(), new MazantiAndersen(), new McCarthyTetrault(), new McConnellValdes(),
            new McDougallGauley(), new McKercher(), new McMillan(), new MeitarLaw(), new MijaresAngoitiaCortesAndFuentes(),
            new Milbank(), new MLTAikins(), new MoalemWeitemeyer(), new Molinari(), new MorganLewis(),
            new MorogluArseven(), new MSP(), new NautaDutilh(), new NelliganLaw(), new NelsonWiliansAndAdvogados(),
            new NielsenNorager(), new Njord(), new Norens(), new NovaLaw(), new NPPLegal(),
            new NysinghAdvocatenNotarissenNV(), new Odigo(), new Ogier(), new OslerHoskinAndHarcourt(), new OyenWiggs(),
            new Paksoy(), new PanettaConsultingGroup(), new Pedersoli(), new Penta(), new PeterAndKim(),
            new Ploum(), new RBK(), new RDJ(), new RitchMueller(), new RitchMuellerAndNicolau(),
            new RobortellaEPeres(), new RonanDalyJermyn(), new RopesAndGray(), new Roschier(), new RPCLegal(),
            new SantamarinaAndSteta(), new SchindlerAttorneys(), new Schoenherr(), new SchurtiPartners(), new Secretariat(),
            new Selmer(), new ShahidLaw(), new SheppardMullin(), new SHorowitzAndCo(), new SIRIUS(),
            new Skadden(), new SmartAndBiggar(), new SpencerWest(), new STBB(), new SteinmetzHaringGurman(),
            new StephensonHarwood(), new StewartMcKelvey(), new Stibbe(), new Tavares(), new TaylorWessing(),
            new TheartMey(), new ThomasBodstrom(), new Titov(), new TucaZbarcea(), new VanDerPutt(),
            new VBAdvocates(), new VieringJentschuraAndPartner(), new Vinge(), new VOPatentsAndTrademarks(), new Walkers(),
            new WatsonFarleyAndWilliams(), new WhiteAndCase(), new WildeboerDellelce(), new WilliamFry(), new Willkie(),
            new WinstonAndStrawn(), new WolfTheiss(), new ZamfirescuRacotiPredoiu(),
            new AlukoAndOyebode(), new HuntonAndrewsKurth(),


            // ByPage
//            new MccannFitzGerald(),
//            new BYRO(),
//            new DavisPolkAndWardwell(), new DebevoiseAndPlimpton(), new PillsburyWinthropShawPittman(), new ProskauerRose(), new SimpsonThacher(),
//
//            // ByNewPage
//            new Provida(), new Vaneps(),

            /* Await firms - Countries to avoid for now ...
            new BaeKimAndLee(), new DRAndAJU() new Deacons(), new CFNLaw(), new MalleyAndCo(),
            new TiruchelvamAssociates(), new MinterEllisonRuddWatts(), new MacphersonKelley(), new DCCLaw(), new MASLaw(),
            new ZhongziLaw(), new GrandwayLaw(), new LeeAndKo(), new MZMLegal(), new SprusonAndFerguson(),
            new K1Chamber(), new ShinAndKim(), new Allens(), new SFKSLaw(), new AlTamimi(),
            new DuncanCotterill(), new OldhamLiAndNie(), new Helmsman(), new RelianceCorporateAdvisors(), new DBHLaw(),
            new MBIP(), new TahotaLaw(), new JamesAndWells(), new HakunLaw(), new PAGBAM(),
            new StikemanElliott(), new RamdasAndWong(), new AguayoEcclefieldAndMartinez(),
            new ConsortiumLegal(), new RennoPenteadoSampaioAdvogados(),
            */

            /** Indian Firms
            new VeritasLegal(), new ALMTLegal(), new AnandAndAnand(),

             * Russia Firms
            new MellingVoitishkinAndPartners()

             * No more lawyers to register
            new FoxAndMandal(),

             * Problems
             *
            */
    };



    public final static Site[] byNewPage = {
            new ACAndR(), new ALGoodbody(), new AOil(), new ArthurCox(), new AsafoAndCo(),
            new Astrea(), new BarneaAndCo(), new BCFLaw(), new Beauchamps(), new Belgravia(),
            new Borenius(), new BurgesSalmon(), new BWBLLP(), new CarneluttiLaw(), new Cobalt(),
            new ControlRisks(), new CRCCD(), new Cuatrecasas(), new DANUBIAPatentAndLaw(), new Dentons(),
            new DGKV(), new Dottir(), new EBN(), new Ekelmans(), new Ellex(),
            new EllisonsSolicitors(), new ENSAfrica(), new EPAndC(), new Esche(), new FCMLimited(),
            new Ferrere(), new Fischer(), new FPSLaw(), new Frontier(), new Fylgia(),
            new GittiAndPartners(), new GORG(), new GornitzkyAndCo(), new Hamso(), new HamsoPatentybra(),
            new HansOffiaAndAssociates(), new HaslingerNagele(), new HiggsAndJohnson(), new Holmes(), new Horten(),
            new Houthoof(), new IbanezParkman(), new JacksonEttiAndEdu(), new JWP(), new KennedyVanderLaan(),
            new KeystoneLaw(), new Kondrat(), new KRBLaw(), new KuriBrena(), new Kvale(),
            new LambadariosLaw(), new LangsethAdvokat(), new LatamLex(), new Legalis(), new Legance(),
            new LePooleBekema(), new LewissSilkin(), new LexCaribbean(), new Lindahl(), new MarksAndClerk(),
            new MBM(), new MdME(), new METIDA(), new MeyerKoring(), new MillerThomsonLLP(),
            new MorrisLaw(), new Mourant(), new MSBSolicitors(), new MyersFletcherAndGordon(), new NaderHayauxAndGoebel(),
            new Noerr(), new OgletreeDeakins(), new OneEssexCourt(), new Onsagers(), new OsborneClarke(),
            new Oxera(), new PaviaAndAnsaldo(), new PearlCohen(), new PhilippeAndPartners(), new PortaAndConsulentiAssociati(),
            new PortolanoCavallo(), new PrasadAndCompany(), new PrinzAndPartner(), new PrueferAndPartner(), new QuinEmanuel(),
            new ReinhardSkuhraWeiseAndPartnerGbR(), new RocaJunyent(), new Sangra(), new SargentAndKrahn(), new SBGK(),
            new Sherrards(), new Sorainen(), new SZA(), new TEMPLARS(), new Thommessen(),
            new ThompsonDorfmanSweatman(), new Vischer(), new WardynskiAndPartners(), new Werksmans(), new Wiersholm(),
            new WikborgRein(), new ZeposAndYannopoulos(), new BDO(),



            /* Await firms - Countries to avoid for now ...
            new NurmansyahAndMuzdalifah(), new TompkinsWake(), new FangdaPartners(), new LonganLaw(), new MishconKaras(),
            new TCLaw(), new DeutschMiller(), new CovenantChambers(), new DWFoxTucker(), new HYLeungAndCo(),
            new RemfryAndSagar(), new Baumgartners(), new SEUM(), new HowseWilliams(), new YoungList(),
            new SabaAndCo(), new GuantaoLaw(), new HuiyeLaw(), new Gadens(), new TannerDeWitt(),
            new RMADVAdvogados(), new BrigrardUrrutia(), new Pulegal(), new Madrona(),
            new EProint(),
            */

            // TEST IT
            //        new Szecskay(),


            /**
             * INDIAN
             * - completed
             * new DSKLegal(),
             *
             * - to complete
             * new HSAAdvocates(), new SamvadPartners(), new JSA()
             * new Poulschmith(), new SimmonsAndSimmons(), new SimmonsWolfhagen(),
             * new DumontBergmanBider(),
            */
    };




    public final static Site[] byFilter = {

    };




    public final static Site[] byClick = {

    };
}