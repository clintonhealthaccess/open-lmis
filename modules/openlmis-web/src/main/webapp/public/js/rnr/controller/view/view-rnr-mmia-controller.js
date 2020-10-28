function ViewRnrMmiaController($scope, $route, Requisitions, messageService, downloadPdfService, downloadSimamService) {
    $scope.rnrLineItems = [];
    $scope.regimens = [];
    $scope.regimeTotalPatients = 0;
    $scope.regimeTotalComunitaryPharmacy = 0;
    $scope.therapeuticLines = [];
    $scope.therapeuticLinesTotalPatients = 0;
    $scope.therapeuticLinesTotalComunitaryPharmacy = 0;
    $scope.typeOfDispensed = undefined;
    $scope.totalTypeOfDispensedDS = undefined;
    $scope.totalTypeOfDispensedDT = undefined;
    $scope.totalTypeOfDispensedDM = undefined;
    $scope.totalTypeOfDispensedWithInTotal = undefined;
    $scope.totalTypeOfDispensedTotal = undefined;
    $scope.newRegimes = [];
    $scope.newProducts = [];
    $scope.isBetweenWebUpgradeAndMobileUpgrade = false;

    var barCodeBackGround = {
        'Adults':"#c7ddbb",
        'Children':"#faf8c7",
        'Solution':"#d3e9f0"
    };
    var openlmisMessageMap = {
        "new": {messageName: "new", tableName: 'Type of patients in TARV'},
        "novos": {messageName: "new", tableName: 'Type of patients in TARV'},
        "maintenance": {messageName: "maintenance", tableName: 'Type of patients in TARV'},
        "manutenção": {messageName: "maintenance", tableName: 'Type of patients in TARV'},
        "alteration": {messageName: "alteration", tableName: 'Type of patients in TARV'},
        "alteração": {messageName: "alteration", tableName: 'Type of patients in TARV'},
        "transit": {messageName: "transit", tableName: 'Type of patients in TARV'},
        "trânsito": {messageName: "transit", tableName: 'Type of patients in TARV'},
        "transfers": {messageName: "transfers", tableName: 'Type of patients in TARV'},
        "transferências": {messageName: "transfers", tableName: 'Type of patients in TARV'},
        "adults": {messageName: "adults", tableName: 'age range of TARV patients'},
        "adultos": {messageName: "adults", tableName: 'age range of TARV patients'},
        "pediatric from 0 to 4 years": {messageName: "4pediatric", tableName: 'age range of TARV patients'},
        "pediátricos 0 aos 4 anos": {messageName: "4pediatric", tableName: 'age range of TARV patients'},
        "pediatric from 5 to 9 years": {messageName: "9pediatric", tableName: 'age range of TARV patients'},
        "pediátricos 5 aos 9 anos": {messageName: "9pediatric", tableName: 'age range of TARV patients'},
        "pediatric from 10 to 14 years": {messageName: "14pediatric", tableName: 'age range of TARV patients'},
        "pediátricos 10 aos 14 anos": {messageName: "14pediatric", tableName: 'age range of TARV patients'},
        "prep": {messageName: "prep", tableName: 'prophylaxis'},
        "ppe": {messageName: "ppe", tableName: 'prophylaxis'},
        "exposed child": {messageName: "exposed", tableName: 'prophylaxis'},
        "criança exposta": {messageName: "exposed", tableName: 'prophylaxis'},
        "total nr of patients in tarv at hf": {messageName: "totalNr", tableName: 'prophylaxis'},
        "total de pacientes em tarv na us": {messageName: "totalNr", tableName: 'prophylaxis'},
    };
    var tableMap = {
        'Type of patients in TRAV': 'patientsType',
        'Tipo de pacientes em TARV': 'patientsType',
        'age range of TRAV patients': 'TARVPatients',
        'Faixa etária dos pacientes em TRAV': 'TARVPatients',
        'prophylaxis': 'prophylaxis',
        'Profilaxia': 'prophylaxis'
    };
    var therapeuticLinesMap = {
        '1st Line': 'view.rnr.mmia.regime.therapeutic.line.1',
        '1ª Linha': 'view.rnr.mmia.regime.therapeutic.line.1',
        '2nd Line': 'view.rnr.mmia.regime.therapeutic.line.2',
        '2ª Linha': 'view.rnr.mmia.regime.therapeutic.line.2',
        '3rd Line': 'view.rnr.mmia.regime.therapeutic.line.3',
        '3ª Linha': 'view.rnr.mmia.regime.therapeutic.line.3'
    };

    $scope.params = {
        'barCodeOptions': {
            lineColor: "#000",
            width: 1,
            height: 40,
            fontSize: 10,
            textAlign: "justify",
            textMargin: 0,
            margin: 0,
            displayValue: true
        },
        'Adults':{
            'productPrimaryName': "bg-color-green align-left",
            'barcode': "bg-color-green",
            'headerclass': "bg-color-green",
            'headername':'Adultos & Adolescentes'
        },
        'Adult':{
            'productPrimaryName': "bg-color-green align-left",
            'barcode': "bg-color-green",
            'headerclass': "bg-color-green",
            'headername':'Adultos & Adolescentes'
        },
        'Children':{
            'productPrimaryName': "bg-color-lightyellow align-left",
            'barcode':"bg-color-lightyellow"
        },
        'Solution':{
            'productPrimaryName': "bg-color-lightblue align-left",
            'barcode':"bg-color-lightblue"
        },
        'Paediatrics':{
            'productPrimaryName':"bg-color-yellow align-left",
            'barcode':'bg-color-yellow',
            'headerclass': "bg-color-yellow rowspan",
            'headername':'Paediatrics'
        },
    };

    $scope.patientsCategory=['patientsType','TARVPatients','prophylaxis'];
    $scope.patient = [];

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadMmiaDetail();
    });

    $scope.$on('messagesPopulated', function () {
        $scope.initMonth();
    });

    $(".btn-download-pdf").hide();
    $(".btn-download-simam").hide();
    $scope.loadMmiaDetail = function () {
        Requisitions.get({ id: $route.current.params.rnr, operation: "skipped" }, function (data) {
            $scope.rnr = data.rnr;
            $scope.year = data.rnr.period.stringYear;
            $scope.newProducts = data.rnr.newProducts;
            $scope.newRegimes = data.rnr.newRegimes;
            $scope.isBetweenWebUpgradeAndMobileUpgrade = data.rnr.patientQuantifications.length === 7 ? data.rnr.clientSubmittedTime > (new Date(messageService.get("app.mmia.layout.number.nine"))).getTime() /*Release date*/ : false;
            $scope.initMonth();
            console.log("isBetweenWebUpgradeAndMobileUpgrade="+$scope.isBetweenWebUpgradeAndMobileUpgrade);
            if ($scope.isBetweenWebUpgradeAndMobileUpgrade){
                $scope.rnr.reportType = "new";
                $scope.initMockedPatient();
                $scope.initMockedProuct();
                $scope.initMockedRegime();
                $scope.initMockedTherapeuticLines();
            } else if (data.rnr.patientQuantifications.length === 7){
                $scope.rnr.reportType = "old";
                $scope.initOldPatient();
                $scope.initProduct();
                $scope.initRegime();
                $scope.initTherapeuticLines();
            } else {
                $scope.rnr.reportType = "new";
                $scope.initPatient();
                $scope.initProduct();
                $scope.initRegime();
                $scope.initTherapeuticLines();
            }

            parseSignature($scope.rnr.rnrSignatures);

            downloadPdfService.init($scope, $scope.rnr.id);
            downloadSimamService.init($scope, $scope.rnr.id);
        });
    };

    function  pickProductsFromResponse(fullSupplyLineItems,productItem, category) {
        var matched = _.find(fullSupplyLineItems, function (item) {
            return item.productCode === productItem.code;
        });
        if (matched){
            return  _.assign({}, matched,{categoryName: category});
        } else {
            return {
                productCode:productItem.code,
                productPrimaryName: productItem.primaryName,
                productStrength: productItem.strength,
                beginningBalance: "N/A",
                quantityReceived: "N/A",
                quantityDispensed: "N/A",
                totalLossesAndAdjustments: "N/A",
                stockInHand: "N/A",
                expirationDate: "",
                categoryName: category
            };
        }
    }

    function  pickRegimeFromResponse(regimenLineItems, regimeItem) {
        var matched = _.find(regimenLineItems, function (item) {
            return item.name === regimeItem.name;
        });
        if (matched) {
            return _.assign({},matched,{comunitaryPharmacy: "N/A"});
        } else {
            return {
                categoryName: regimeItem.code.startsWith("Adults") ? "Adults" : "Paediatrics",
                name: regimeItem.name,
                comunitaryPharmacy: "N/A",
                patientsOnTreatment: "N/A"
            };
        }
    }

    $scope.initMockedPatient = function () {
        var messageMap = [
            {messageName: "view.rnr.mmia.patient.new", header: 'view.rnr.mmia.patient.header.patientsType', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.maintenance", header: 'view.rnr.mmia.patient.header.patientsType', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.alteration", header: 'view.rnr.mmia.patient.header.patientsType', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.transit", header: 'view.rnr.mmia.patient.header.patientsType', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.transfers", header: 'view.rnr.mmia.patient.header.patientsType', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.adults", header: 'view.rnr.mmia.patient.header.TARVPatients', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.4pediatric", header: 'view.rnr.mmia.patient.header.TARVPatients', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.9pediatric", header: 'view.rnr.mmia.patient.header.TARVPatients', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.14pediatric", header: 'view.rnr.mmia.patient.header.TARVPatients', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.prep", header: 'view.rnr.mmia.patient.header.prophylaxis', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.ppe", header: 'view.rnr.mmia.patient.header.prophylaxis', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.exposed", header: 'view.rnr.mmia.patient.header.prophylaxis', total: "N/A"},
            {messageName: "view.rnr.mmia.patient.totalNr", header: 'view.rnr.mmia.patient.header.prophylaxis', total: "N/A"}
        ];
        $scope.rnr.patientQuantifications = _.groupBy(messageMap, 'header');
        $scope.totalTypeOfDispensedDS = "N/A";
        $scope.totalTypeOfDispensedDT = "N/A";
        $scope.totalTypeOfDispensedDM = "N/A";
        $scope.totalTypeOfDispensedWithInTotal = "N/A";
        $scope.totalTypeOfDispensedTotal = "N/A";

    };

    var addEmptyLinesForRegimes = function(regimens) {
        if (regimens.Adults === undefined) {
            regimens.Adults = [];
        }

        if (regimens.Paediatrics === undefined) {
            regimens.Paediatrics = [];
        }

        regimens.Adults.push({categoryName: 'Adults'});
        regimens.Adults.push({categoryName: 'Adults'});

        regimens.Paediatrics.push({categoryName: 'Paediatrics'});
        regimens.Paediatrics.push({categoryName: 'Paediatrics'});
    };

    $scope.initMockedRegime = function () {
        var regimens1 = _.map($scope.newRegimes, function (item) {
            return pickRegimeFromResponse($scope.rnr.regimenLineItems, item);
        });
        regimens = _.groupBy(regimens1, function (item) {
            return item.categoryName;
        });
        addEmptyLinesForRegimes(regimens);
        $scope.regimens = regimens;
        if ($scope.rnr.regimenLineItems[0].comunitaryPharmacy !== undefined) {
            $scope.regimeTotalComunitaryPharmacy = sumFunc($scope.rnr.regimenLineItems,'comunitaryPharmacy');
        }
        if ($scope.rnr.therapeuticLines !== undefined) {
            $scope.therapeuticLinesTotalPatients = sumFunc($scope.rnr.therapeuticLines,'patientsOnTreatment');
            $scope.therapeuticLinesTotalComunitaryPharmacy = sumFunc($scope.rnr.therapeuticLines,'comunitaryPharmacy');
        }
    };

    $scope.initMockedTherapeuticLines =  function () {
        $scope.therapeuticLines =[
            {code: 'view.rnr.mmia.regime.therapeutic.line.1',comunitaryPharmacy: 'N/A', patientsOnTreatment: 'N/A'},
            {code: 'view.rnr.mmia.regime.therapeutic.line.2', comunitaryPharmacy: 'N/A', patientsOnTreatment: 'N/A'},
            {code: 'view.rnr.mmia.regime.therapeutic.line.3',comunitaryPharmacy: 'N/A', patientsOnTreatment: 'N/A'}
        ];
    };
    $scope.initMockedProuct = function () {
        var fullSupplyLineItems = _.sortBy($scope.newProducts,'code');
        fullSupplyLineItems = _.map(fullSupplyLineItems, function (item) {
            return pickProductsFromResponse($scope.rnr.fullSupplyLineItems, item.productItem, item.categoryName);
        });
        addEmptyLine(fullSupplyLineItems);
    };

    function getBarCodeOptions(product){
        return _.assign({},$scope.params.barCodeOptions,{background: barCodeBackGround[product.productType], text: "* " + product.productCode + " *"});
    }

    $scope.generateBarcode = function (product) {
        if (product.productCode === '') return;
        JsBarcode("#barcode"+product.productCode, product.productCode , getBarCodeOptions(product));
    };

    function parseSignature(signatures) {
        _.forEach(signatures, function (signature) {
            if (signature.type === "SUBMITTER") {
                $scope.submitterSignature = signature.text;
            } else if (signature.type === "APPROVER") {
                $scope.approverSignature = signature.text;
            }
        });
    }

    $scope.initTherapeuticLines = function () {
        $scope.therapeuticLines = _.map($scope.rnr.therapeuticLines,
            function (value) {
                value.code = therapeuticLinesMap[value.code];
                return value;
            });
    };

    $scope.initMonth = function () {
        var month = "month." + $scope.rnr.period.stringEndDate.substr(3, 2);
        $scope.month = messageService.get(month);
    };

    function addEmptyLine(fullSupplyLineItems) {

        fullSupplyLineItems = _.groupBy(fullSupplyLineItems, function (item) {
            return item.categoryName;
        });

        if (fullSupplyLineItems.Adult === undefined) {
            fullSupplyLineItems.Adult = [];
        }

        if (fullSupplyLineItems.Children === undefined) {
            fullSupplyLineItems.Children = [];
        }

        if (fullSupplyLineItems.Solution === undefined) {
            fullSupplyLineItems.Solution = [];
        }

        fullSupplyLineItems.Adult.push({ categoryName: 'Adult' });
        fullSupplyLineItems.Adult.push({ categoryName: 'Adult' });
        fullSupplyLineItems.Children.push({ categoryName: 'Children' });
        fullSupplyLineItems.Solution.push({ categoryName: 'Solution' });

        $scope.rnrLineItems = $scope.rnrLineItems.concat(fullSupplyLineItems.Adult, fullSupplyLineItems.Children, fullSupplyLineItems.Solution);
    }

    $scope.initProduct = function () {
        var fullSupplyLineItems = _.sortBy($scope.rnr.fullSupplyLineItems, 'productCode');

        for (var i = 0; i < fullSupplyLineItems.length; i++) {
            formatExpirationDate(fullSupplyLineItems[i]);
        }

        addEmptyLine(fullSupplyLineItems);
    };


    $scope.initOldPatient = function () {
        var patientQuantifications = $scope.rnr.patientQuantifications;
        var openlmisMessageMap = {
            "New": "new",
            "Maintenance": "maintenance",
            "Alteration": "alteration",
            "PTV": "ptv",
            "PPE": "ppe",
            "Total Dispensed": "dispensed",
            "Total Patients": "total",
            "Novos": "new",
            "Manutenção": "maintenance",
            "Alteração": "alteration",
            "Total de Meses dispensados": "dispensed",
            "Total de pacientes em TARV na US": "total"
        };
        for (var i = 0; i < patientQuantifications.length; i++) {
            var item = patientQuantifications[i];
            item.category = "view.rnr.mmia.patient." + openlmisMessageMap[item.category];
        }
    };

    $scope.initPatient = function () {
        var invalideKey = _.keys(openlmisMessageMap);
        formatTypeOfDispensed($scope.rnr.patientQuantifications);
        var patientQuantifications = _.filter($scope.rnr.patientQuantifications, function (value) {
            var lowerCategory = value.category && value.category.toLocaleLowerCase() || '';
            return _.includes(invalideKey, lowerCategory);
        });
        patientQuantifications = _.map(patientQuantifications, function (value) {
            var category = openlmisMessageMap[value.category.toLocaleLowerCase()];
            var tableName = value.tableName ? value.tableName : category.tableName;
            return _.assign({}, value, {
                header: "view.rnr.mmia.patient.header." + tableMap[tableName],
                messageName: "view.rnr.mmia.patient." + category.messageName
            });
        });
        patientQuantifications = _.groupBy(patientQuantifications, 'header');
        $scope.rnr.patientQuantifications = patientQuantifications;
    };

    var formatTypeOfDispensed = function (patientQuantifications) {
        $scope.typeOfDispensed = _.filter(patientQuantifications,function (item) {
            return item.tableName === "Type of Dispensed" || item.tableName === "Type of Dispensed";
        });
        if ($scope.typeOfDispensed !== undefined) {
            $scope.totalTypeOfDispensedDS = getDataByKey('dispensed_ds5') +
                getDataByKey('dispensed_ds4') +
                getDataByKey('dispensed_ds3') +
                getDataByKey('dispensed_ds2') +
                getDataByKey('dispensed_ds1') +
                getDataByKey('dispensed_ds');
            $scope.totalTypeOfDispensedDT = getDataByKey('dispensed_dt2') +
                getDataByKey('dispensed_dt1') +
                getDataByKey('dispensed_dt');
            $scope.totalTypeOfDispensedDM = getDataByKey('dispensed_dm');
            $scope.totalTypeOfDispensedWithInTotal = getDataByKey('dispensed_ds') +
                getDataByKey('dispensed_dt') +
                getDataByKey('dispensed_dm');
        }
    };

    function getDataByKey(nameKey){
        if ($scope.isBetweenWebUpgradeAndMobileUpgrade) {
            return "N/A";
        }
        for (var i=0; i < $scope.typeOfDispensed.length; i++) {
            if ($scope.typeOfDispensed[i].category === nameKey) {
                return $scope.typeOfDispensed[i].total;
            }
        }
    }

    $scope.getDataByKey = function (nameKey, myArray) {
        if ($scope.isBetweenWebUpgradeAndMobileUpgrade) {
            return "N/A";
        }
        for (var i =0; i< myArray.length; i++){
            if (myArray[i].category === nameKey){
                return myArray[i].total;
            }
        }
    };

    $scope.formatAdjustMent = function(totalTreatments, totalWithInMonths){
        return parseFloat(totalTreatments/totalWithInMonths).toFixed(2);
    };

    var formatExpirationDate = function (theOneItem) {
        if (theOneItem.expirationDate) {
            var splitDate = theOneItem.expirationDate.split('/');
            var yearNumber = splitDate[2];
            var monthNumber = splitDate[1];
            var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            theOneItem.expirationDate = monthNames[monthNumber - 1] + " " + yearNumber;
        }
    };

    $scope.initRegime = function () {
        var regimens = _.groupBy($scope.rnr.regimenLineItems, function (item) {
            return item.categoryName;
        });
        addEmptyLinesForRegimes(regimens);
        $scope.regimens = $scope.rnr.reportType === 'new' ? regimens : $scope.regimens.concat(regimens.Adults, regimens.Paediatrics);
        $scope.regimeTotalPatients= sumFunc($scope.rnr.regimenLineItems,'patientsOnTreatment');
        if ($scope.rnr.regimenLineItems[0].comunitaryPharmacy !== undefined) {
            $scope.regimeTotalComunitaryPharmacy = sumFunc($scope.rnr.regimenLineItems,'comunitaryPharmacy');
        }
        if ($scope.rnr.therapeuticLines !== undefined) {
            $scope.therapeuticLinesTotalPatients = sumFunc($scope.rnr.therapeuticLines,'patientsOnTreatment');
            $scope.therapeuticLinesTotalComunitaryPharmacy = sumFunc($scope.rnr.therapeuticLines,'comunitaryPharmacy');
        }
    };

    var sumFunc = function (keyValue, key) {
        var sum = 0;
        _.forEach(keyValue, function (item) {
            sum +=item[key];
        });
        return sum;
    };
}
