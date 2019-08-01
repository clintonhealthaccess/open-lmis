function ViewRnrMmiaController($scope, $route, Requisitions, messageService, downloadPdfService, downloadSimamService) {
    $scope.rnrLineItems = [];
    $scope.regimens =[];
    $scope.regimeTotal = 0;
    $scope.therapeuticLines =[{
        name: '1st Line',
        patientsOnTreatment: 12,
        patientsOnComunitary: 20
    }, {
        name: '2nd Lines',
        patientsOnTreatment: 22,
        patientsOnComunitary: 30
    }, {
        name: '3rd lines',
        patientsOnTreatment: 32,
        patientsOnComunitary: 40
    }];

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
        Requisitions.get({id: $route.current.params.rnr, operation: "skipped"}, function (data) {
            $scope.rnr = data.rnr;
            $scope.year = data.rnr.period.stringYear;

            $scope.initMonth();
            $scope.initProduct();
            $scope.initRegime();
            $scope.initPatient();

            parseSignature($scope.rnr.rnrSignatures);

            downloadPdfService.init($scope, $scope.rnr.id);
            downloadSimamService.init($scope, $scope.rnr.id);
        });
    };

    function parseSignature(signatures) {
        _.forEach(signatures, function (signature) {
            if (signature.type == "SUBMITTER") {
                $scope.submitterSignature = signature.text;
            } else if (signature.type == "APPROVER") {
                $scope.approverSignature = signature.text;
            }
        });
    }

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

        fullSupplyLineItems.Adult.push({categoryName: 'Adult'});
        fullSupplyLineItems.Adult.push({categoryName: 'Adult'});
        fullSupplyLineItems.Children.push({categoryName: 'Children'});
        fullSupplyLineItems.Solution.push({categoryName: 'Solution'});

        $scope.rnrLineItems = $scope.rnrLineItems.concat(fullSupplyLineItems.Adult, fullSupplyLineItems.Children, fullSupplyLineItems.Solution);
    }

    $scope.initProduct = function () {
        var fullSupplyLineItems = _.sortBy($scope.rnr.fullSupplyLineItems, 'productCode');

        for (var i = 0; i < fullSupplyLineItems.length; i++) {
            formatExpirationDate(fullSupplyLineItems[i]);
        }

        addEmptyLine(fullSupplyLineItems);
    };

    $scope.initPatient = function () {
        var openlmisMessageMap = {
            "new": {messageName:"new", tableName: 'Type of patients in TARV'},
            "novos": {messageName:"new", tableName: 'Type of patients in TARV'},
            "maintenance": {messageName:"maintenance", tableName: 'Type of patients in TARV'},
            "manutenção": {messageName:"maintenance", tableName: 'Type of patients in TARV'},
            "alteration": {messageName:"alteration", tableName: 'Type of patients in TARV'},
            "alteração": {messageName:"alteration", tableName: 'Type of patients in TARV'},
            "transit": {messageName:"transit", tableName: 'Type of patients in TARV'},
            "trânsito": {messageName:"transit", tableName: 'Type of patients in TARV'},
            "transfers": {messageName:"transfers", tableName: 'Type of patients in TARV'},
            "transferências": {messageName:"transfers", tableName: 'Type of patients in TARV'},
            "3 mopnths dispense (dt)": {messageName:"3dt", tableName: 'Months dispensed'},
            "dispensa para 3 meses (dt)": {messageName:"3dt", tableName: 'Months dispensed'},
            "month dispense": {messageName:"md", tableName: 'Months dispensed'},
            "dispensa mensal (dm)": {messageName:"md", tableName: 'Months dispensed'},
            "# of therapeutic months dispensed": {messageName:"therapeuticDt", tableName: 'Months dispensed'},
            "#meses de terapêutica dispensados": {messageName:"therapeuticDt", tableName: 'Months dispensed'},
            "adults": {messageName:"adults", tableName: 'age range of TARV patients'},
            "adultos": {messageName:"adults", tableName: 'age range of TARV patients'},
            "pediatric from 0 to 4 years": {messageName:"4pediatric", tableName: 'age range of TARV patients'},
            "pediátricos 0 aos 4 anos": {messageName:"4pediatric", tableName: 'age range of TARV patients'},
            "pediatric from 5 to 9 years": {messageName:"9pediatric", tableName: 'age range of TARV patients'},
            "pediátricos 5 aos 9 anos": {messageName:"9pediatric", tableName: 'age range of TARV patients'},
            "pediatric from 10 to 14 years": {messageName:"14pediatric", tableName: 'age range of TARV patients'},
            "pediátricos 10 aos 14 anos": {messageName:"14pediatric", tableName: 'age range of TARV patients'},
            "prep": {messageName:"prep", tableName: 'prophylaxis'},
            "ppe": {messageName:"ppe", tableName: 'prophylaxis'},
            "exposed child": {messageName:"exposed", tableName: 'prophylaxis'},
            "criança exposta": {messageName:"exposed", tableName: 'prophylaxis'},
            "total nr of patients in tarv at hf": {messageName:"totalNr", tableName: 'prophylaxis'},
            "total de pacientes em tarv na us": {messageName:"totalNr", tableName: 'prophylaxis'},
        };
        var tableMap = {
            'Type of patients in TARV': 'patientsType',
            'Months dispensed': 'md',
            'age range of TARV patients': 'TARVPatients',
            'prophylaxis': 'prophylaxis'
        };
        var invalideKey = _.keys(openlmisMessageMap);
        var patientQuantifications = _.filter($scope.rnr.patientQuantifications, function(value) {
            const lowerCategory = value.category && value.category.toLocaleLowerCase()||''
            return _.includes(invalideKey, lowerCategory);
        });
        patientQuantifications = _.map(patientQuantifications, function(value) {
            var category = openlmisMessageMap[value.category.toLocaleLowerCase()];
            var tableName = value.tableName ? value.tableName : category.tableName
            return _.assign({}, value, {
                header: "view.rnr.mmia.patient.header." + tableMap[tableName],
                messageName: "view.rnr.mmia.patient." + category.messageName,
            })
        });
        patientQuantifications = _.groupBy(patientQuantifications, 'header');
        $scope.rnr.patientQuantifications = patientQuantifications;
    };

    var formatExpirationDate = function (theOneItem) {
        if (theOneItem.expirationDate) {
            var splitDate = theOneItem.expirationDate.split('/');
            var yearNumber = splitDate[2];
            var monthNumber = splitDate[1];

            var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            ];

            theOneItem.expirationDate = monthNames[monthNumber - 1] + " " + yearNumber;
        }
    };


    $scope.initRegime = function () {
        var regimens = _.groupBy($scope.rnr.regimenLineItems, function (item) {
            return item.categoryName;
        });

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

        $scope.regimens = $scope.regimens.concat(regimens.Adults, regimens.Paediatrics);
        calculateRegimeTotal($scope.rnr.regimenLineItems);
    };

    var calculateRegimeTotal = function (regimens) {
        for (var i = 0; i < regimens.length; i++) {
            $scope.regimeTotal += regimens[i].patientsOnTreatment;
        }
    };

}
